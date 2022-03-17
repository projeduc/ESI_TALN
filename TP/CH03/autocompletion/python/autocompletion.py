#!/usr/bin/env python
# -*- coding: utf-8 -*-

import math
import json
import random

class LaplaceBiGram:

    def __init__(self):
        self.uni_grams = {'<s>': 0}
        self.bi_grams = {}

    # TODO : compléter cette méthode
    def entrainer(self, data):
        pass

    # TODO : compléter cette méthode
    def noter(self, past, current):
        return 0.0

    # TODO : compléter cette méthode
    def estimer(self, mots):
        mots_scores = []
        # ajouter des tuples (mot, score) à cette liste
        return sorted(mots_scores, key=lambda tab: tab[1], reverse=True)

    def exporter_json(self):
        return self.__dict__.copy()

    def importer_json(self, data):
        for cle in data:
            self.__dict__[cle] = data[cle]

class Autocompletion():
    def __init__(self):
        self.modele = LaplaceBiGram()
        self.eval = []

    def entrainer(self, url):
        f = open(url, 'r')
        data = []
        for l in f: # la lecture ligne par ligne
            phrase = l.strip().lower().split()
            if len(phrase) > 0:
                data.append(phrase)
        f.close()
        self.modele.entrainer(data)

    def estimer(self, phrase, nbr):
        mots_scores = self.modele.estimer(phrase.strip().lower().split())
        return mots_scores[:nbr]


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
        f = open(url, 'r')
        for l in f: # la lecture ligne par ligne
            info = l.strip().lower().split("	")
            if len(info) < 2 :
                continue
            self.eval.append(info)

    def evaluer(self, n, m): #Mean reciprocal rank
        if n == -1:
            S = self.eval
            n = len(S)
        else :
            S = random.sample(self.eval, n)
        score = 0.0
        for i in range(n):
            test = S[i]
            print('p(', test[1], '|',test[0], ')')
            res = self.estimer(test[0], m)
            print('found:', res)
            words = [e[0] for e in res]
            try:
                i = words.index(test[1]) + 1
                score += 1/i
            except ValueError:
                pass
        score = score/n
        print('Score = ', score)
        return score

if __name__ == '__main__':
    program = Autocompletion()
    program.entrainer('data/algerie_train.txt')
    program.sauvegarder_modele('./tp.json')
    #phrase = "L' Algérie faisant partie du"
    #res = program.estimer(phrase, 10)
    #print(res)
    program.charger_evaluation('data/algerie_test.txt')
    # 80 exemplaires ; 10 résultats possibles
    mrr = program.evaluer(80, 10)
