#!/usr/bin/env python
# -*- coding: utf-8 -*-

import math
import json
import random
from typing import Dict, List, Tuple, Set

def init_inc(mp, cle, init_val=1.0, inc_val=1.0):
    if cle in mp:
        mp[cle] += inc_val
    else:
        mp[cle] = init_val

class HMM:

    def __init__(self, tags: List[str]):
        """_summary_

        :param tags: _description_
        :type tags: List[str]
        """

        # Tag_i --> tag_i+1 --> prob
        self.transition : Dict[str, Dict[str, float]] = {} # probabilités de transition
        # Tag --> mot --> prob
        self.emission : Dict[str, Dict[str, float]] = {} # probabilité d'émission
        # Tag --> prob
        self.init : Dict[str, float] = {} # probabilité initiale des états

        # score des mots non trouvées dans l'emission
        # unk_score = 1/|V|
        self.unk_emission = 0.0

        self.tags = tags

        for tag in tags:
            self.transition[tag] = {}
            self.emission[tag] = {}
            self.init[tag] = 0.0
            for tag_suiv in tags:
                self.transition[tag][tag_suiv] = 0.0

    def entrainer(self, data : List[List[Tuple[str, str]]]) -> None :

        tags_freq = {}
        vocab = set()
        for phrase in data:
            mot_prec, tag_prec = phrase[0]
            vocab.add(mot_prec)
            init_inc(self.init, tag_prec)
            init_inc(tags_freq, tag_prec)
            init_inc(self.emission[tag_prec], mot_prec)

            for j in range(1, len(phrase)):
                mot, tag = phrase[j]
                vocab.add(mot)
                # transition tag_prec --> tag
                init_inc(self.transition[tag_prec], tag)

                # émission tag --> mot
                init_inc(self.emission[tag], mot)

                init_inc(tags_freq, tag)

                mot_prec, tag_prec = mot, tag

        V = len(vocab)
        self.unk_emission = 1/V
        Vt = len(self.tags)
        nbr_phrases = len(data)
        for tag in self.tags:
            self.init[tag] = (self.init[tag]+1)/(nbr_phrases+Vt)
            nbr_tag = tags_freq[tag]
            emission_tag = self.emission[tag]
            for mot in emission_tag :
                emission_tag[mot] = (emission_tag[mot]+1)/(nbr_tag + V)
            transition_tag = self.transition[tag]
            for tag_suiv in self.tags:
                transition_tag[tag_suiv] = (transition_tag[tag_suiv]+1)/(nbr_tag+Vt)

    # TODO: compléter cette méthode
    def noter(self, past_pos: str, current_pos: str, mot: str) -> float:
        return math.log(1)

    def estimer_force_brute(self, phrase: List[str]) -> List[str]:
        tags_max = []
        score_max = - math.inf
        pile = [('', 0, 0.0)]
        T = len(phrase)
        N = len(self.tags)
        while len(pile) > 0 :
            i = len(pile) - 1
            past_tag, t, score = pile.pop()
            pile.append((past_tag, t+1, score))
            # forward
            while i < T:
                tag = self.tags[t]
                mot = phrase[i]
                score += self.noter(past_tag, tag, mot)
                pile.append((tag, 1, score))

                i += 1
                t = 0
                past_tag = tag
            #print(pile)
            if score_max < score:
                #print(pile)
                score_max = score
                tags_max, _, _ = zip(*pile[1:])
                #print(score_max)
                
            # backward
            pile.pop() # dépiler le dernier élément
            while len(pile) > 0 and pile[-1][1] == N:
                pile.pop()

        return list(tags_max)

    def estimer_gourmand(self, phrase: List[str]) -> List[str]:
        tags = []
        past_tag = ''
        for mot in phrase:
            max_score = - math.inf
            max_tag = ''
            for tag in self.tags:
                score = self.noter(past_tag, tag, mot)
                if score > max_score:
                    max_score = score
                    max_tag = tag
            tags.append(max_tag)
            past_tag = max_tag
        return tags

    # TODO compléter cette méthode : vous devez suivre la même structure définie ici
    def estimer_viterbi(self, phrase: List[str]) -> List[str] :

        N = len(self.tags)
        T = len(phrase)
        viterbi = [[-math.inf]*T for i in range(N)]
        backpointer = [[0]*T for i in range(N)]

        tags = []
        
        return tags

    def exporter_json(self):
        return self.__dict__.copy()

    def importer_json(self, data):
        for cle in data:
            self.__dict__[cle] = data[cle]

class MorphoSyntaxe:
    def __init__(self):
        self.eval = []

    @staticmethod
    def lire_phrases_etiquetees(url: str) -> Tuple[List[str], List[str]] :
        
        f = open(url, 'r')
        data = []
        tags = set()
        for l in f: # la lecture ligne par ligne
            if len(l) < 5 :# si la longueur est 
                continue
            # on supprime les espaces auteur de la ligne, on convertit les caractères een minuscule, 
            # on remplace 
            phrase = l.strip().lower().replace('\/', '\\').split()
            p = []
            data.append(p)
            for mottag in phrase:
                mot, tag = mottag.split("/")
                p.append((mot.replace('\\', '/'), tag))
                tags.add(tag)
        f.close()
        return data, list(tags)

    def entrainer(self, url: str) -> None:
        data, tags = self.__class__.lire_phrases_etiquetees(url)

        self.modele = HMM(tags)
        self.modele.entrainer(data)

    def _estimer(self, phrase: List[str], opt: str = 'forcebrute') -> List[Tuple[str, str]]:
        if opt == 'viterbi':
            tags = self.modele.estimer_viterbi(phrase)
        elif opt == 'gourmand':
            tags = self.modele.estimer_gourmand(phrase)
        else :
            tags = self.modele.estimer_force_brute(phrase)
        # res = []
        # for i in range(len(phrase)):
        #     res.append((phrase[i], tags[i]))
        return tags
        #return list(zip(phrase, tags))

    def estimer(self, phrase: str, opt: str = 'bruteforce') -> List[Tuple[str, str]]:
        return self._estimer(phrase.strip().lower().split(), opt=opt)

    def charger_modele(self, url):
        f = open(url, 'r')
        data = json.load(f)
        self.modele.importer_json(data)
        f.close()

    def sauvegarder_modele(self, url):
        f = open(url, 'w')
        json.dump(self.modele.exporter_json(), f)
        f.close()

    def charger_evaluation(self, url):
        senttags, _ = self.__class__.lire_phrases_etiquetees(url)
        for senttag in senttags:
            sents, tags = zip(*senttag)
            self.eval.append((list(sents), list(tags)))
        #self.eval = [(zip(*senttag)) for senttag in senttags]

    def evaluer_phrase(self, sysTags, refTags):
        INT = 0.0
        for i in range(len(sysTags)):
            if sysTags[i] == refTags[i]:
                INT += 1.0
        return INT/len(refTags)

    def evaluer(self, n): #Moyennes des R, P, F1
        if n == -1:
            S = self.eval
            n = len(S)
        else :
            S = random.sample(self.eval, n)
        score_gourmand = 0.0
        score_viterbi = 0.0
        score_force_brute = 0.0
        for i in range(n):
            test = S[i]
            print('=======================')
            print('phrase :', test[0])
            print('tags de référence :', test[1])

            # sysTags = self._estimer(test[0])
            # print('brute force :', sysTags)
            # score_i = self.evaluer_phrase(sysTags, test[1])
            # print('score :', score_i)
            # score_force_brute += score_i

            sysTags = self._estimer(test[0], opt="viterbi")
            print('viterbi :', sysTags)
            score_i = self.evaluer_phrase(sysTags, test[1])
            print('score :', score_i)
            score_viterbi += score_i

            sysTags = self._estimer(test[0], opt="gourmand")
            print('gourmand :', sysTags)
            score_i = self.evaluer_phrase(sysTags, test[1])
            print('score :', score_i)
            score_gourmand += score_i

        score_gourmand = score_gourmand/n
        score_viterbi = score_viterbi/n
        score_force_brute /= n
        print('----------- Scores totaux -------------')
        print('score gourmand : ', score_gourmand)
        print('score viterbi : ', score_viterbi)
        print('score force brute : ', score_force_brute)


def tester_noter():
    annotateur = MorphoSyntaxe()
    annotateur.entrainer('./data/test.txt')
    phrase = 'il peut aider'
    tags = ['det', 'verb', 'verb']
    scores = [-3.465735902799726, -3.401197381662155, -3.3141860046725258]
    mots = phrase.strip().lower().split()
    past_tag=''
    for mot, tag, score in zip(mots, tags, scores):
        print('mot=', mot, ', tag=',tag, ', score=', score, ', mon score =', annotateur.modele.noter(past_tag, tag, mot))
        past_tag=tag

def tester_viterbi():
    annotateur = MorphoSyntaxe()
    annotateur.entrainer('./data/univ_train.txt')
    phrase = 'this has some logic .'
    print('viterbi doit être comme force brute')
    print('force brute : ', annotateur.estimer(phrase))
    print('gourmand : ', annotateur.estimer(phrase, opt='gourmand'))
    print('viterbi : ', annotateur.estimer(phrase, opt='viterbi'))


def evaluation():
    annotateur = MorphoSyntaxe()
    annotateur.entrainer('./data/univ_train.txt')
    annotateur.charger_evaluation('./data/univ_test.txt')
    annotateur.evaluer(-1)


if __name__ == '__main__':
    tester_noter()
    # tester_viterbi()
    # evaluation()
