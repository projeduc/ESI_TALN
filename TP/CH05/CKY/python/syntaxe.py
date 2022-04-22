#!/usr/bin/env python
# -*- coding: utf-8 -*-

import re
import random
from typing import Dict, List, Tuple, Set


# ======================================================
#                   Analyse CKY
# ======================================================

class CKY:
    def __init__(self, gram: List[Tuple[str, str, str]], lex: Dict[str, List[str]]):
        # Grammaire
        self.gram = gram # liste((A, B, C)) où A -> B C
        self.lex = lex # mot --> liste(PoS)

    # TODO: Compléter cette méthode
    def analyser(self, phrase: List[str]) -> List[List[List[Tuple[str, int, int, int]]]]:
        T = []
        N = len(phrase)
        for i in range(N):
            T.append([[] for i in range(N)])
            mot = phrase[i]
            for A in self.lex[mot]: # ici, on suppose que le mot existe dans la lexique ; sinon, on aura une erreur
                T[i][i].append((A, -1, -1, -1)) # On ajoute toutes les variables possibles
            
        # Compléter ici : remplissage 

        return T

    def exporter_json(self):
        return self.__dict__.copy()

    def importer_json(self, data):
        for cle in data:
            self.__dict__[cle] = data[cle]

# ======================================================
#                Analyse syntaxique
# ======================================================

# TODO: compléter cette fonction
def evaluer_stricte(ref, sys):
    return 0

def construire(T, phrase, i, j, pos):
    A, k, iB, iC = T[i][j][pos]
    #A = A.upper()
    if k >= 0:
        gauche = construire(T, phrase, i, k, iB)
        droit = construire(T, phrase, k+1, j, iC)
        return (A, gauche, droit)
    return (A, phrase[i])

def parse_tuple(string):
    string = re.sub('([^\s(),]+)', "'\\1'", string)
    #print(string)
    try:
        s = eval(string)
        if type(s) == tuple:
            return s
        return
    except:
        return

class Syntaxe():
    def __init__(self):
        self.eval = []

    def _analyser(self, phrase):
        r = None
        T = self.modele.analyser(phrase)
        n = len(phrase) - 1
        # for i in range(n+1):
        #     for j in range(i, n+1):
        #         print(i+1, j+1, T[i][j])
        for pos in range(len(T[0][n])):
            if T[0][n][pos][0] == 'S':
                # print('bingo')
                r = construire(T, phrase, 0, n, pos)
                break
        return r

    def analyser(self, phrase: str):
        return self._analyser(phrase.strip().lower().split())

    def charger_modele(self, url):
        f = open(url, 'r')
        lex = {}
        gram = []
        for l in f: # la lecture ligne par ligne
            l = l.strip()
            if len(l) < 5 or l.startswith('#') :
                continue
            info = l.split('	')
            if len(info) == 2:
                if not info[1] in lex:
                    lex[info[1]] = []
                lex[info[1]].append(info[0])
            elif len(info) == 3:
                gram.append((info[0], info[1], info[2]))
        self.modele = CKY(gram, lex)
        f.close()

    def charger_evaluation(self, url):
        f = open(url, 'r')
        for l in f: # la lecture ligne par ligne
            l = l.strip()
            if len(l) < 5 or l.startswith('#'):
                continue
            info = l.split('	')
            
            self.eval.append((info[0], parse_tuple(info[1])))

    def evaluer(self, n):
        if n == -1:
            S = self.eval
            n = len(S)
        else :
            S = random.sample(self.eval, n)
        score = 0.0
        for i in range(n):
            test = S[i]
            print('=======================')
            print('phrase :', test[0])
            print('arbre de référence :', test[1])
            arbre = self.analyser(test[0])
            print('arbre du système :', arbre)
            score_i = evaluer_stricte(test[1], arbre)
            print('score :', score_i)
            score += score_i

        score = score/n
        print('---------------------------------')
        print('score total : ', score)



# ======================================================
#        Génération du graphique (langage Dot)
# ======================================================

def generer_noeud(noeud, id=0):
    # Si le noeud n'existe pas
    if noeud is None:
        return 0, ''
    # Si le noeud est final,
    nid = id + 1
    if len(noeud) < 3:
        return nid, 'N' + str(id) + '[label="' + noeud[0] + "=" + noeud[1] + '" shape=box];\n'
    # Sinon,
    # s'il y a des fils, on boucle sur les fils et on imprime des SI ... ALORS
    res = 'N' + str(id) + '[label="' + noeud[0] + '"];\n'
    nid_g = nid
    nid, code = generer_noeud(noeud[1], id=nid_g)
    res += code
    res += 'N' + str(id) + ' ->  N' + str(nid_g) + ';\n'
    nid_d = nid
    nid, code = generer_noeud(noeud[2], id=nid_d)
    res += code
    res += 'N' + str(id) + ' ->  N' + str(nid_d) + ';\n'
    return nid, res

def generer_graphviz(racine, url):
    res = 'digraph Tree {\n'
    id, code = generer_noeud(racine)
    res += code
    res += '}'
    f = open(url, 'w')
    f.write(res)
    f.close()


# ======================================================
#                       TESTES
# ======================================================

# le test du cours pour tester l'analyseur CKY
def tester_cky():
    parser = Syntaxe()
    parser.charger_modele('data/gram1.txt')
    phrase = 'la petite forme une petite phrase'
    resultat = "('S', ('NP', ('DET', 'la'), ('N', 'petite')), ('VP', ('V', 'forme'), ('NP', ('DET', 'une'), ('AP', ('ADJ', 'petite'), ('N', 'phrase')))))"
    arbre = parser.analyser(phrase)
    print('résultat attendu : ', resultat)
    print('mon     résultat : ', arbre)
    generer_graphviz(arbre, 'arbre_python.gv')

# tester l'évaluation stricte
def tester_evaluer_arbre():
    print('résultat attendu : 0, résultat trouvé : ', evaluer_stricte(None, ('A', 'd')))
    print('résultat attendu : 1, résultat trouvé : ', evaluer_stricte(None, None))
    print('résultat attendu : 0, résultat trouvé : ', evaluer_stricte(('A', 'a'), ('A', ('B', 'b'), ('C', 'c'))))
    print('résultat attendu : 0, résultat trouvé : ', evaluer_stricte(('A', ('B', 'b'), ('C', 'c')), ('A', 'a')))
    print('résultat attendu : 0, résultat trouvé : ', evaluer_stricte(('A', ('B', 'b'), ('C', 'c')), ('A', ('B', 'd'), ('C', 'c'))))
    print('résultat attendu : 1, résultat trouvé : ', evaluer_stricte(('A', ('B', 'b'), ('C', 'c')), ('A', ('B', 'b'), ('C', 'c'))))

# tester l'évaluation stricte
# Le résultat doit être 1
def tester_evaluation():
    parser = Syntaxe()
    parser.charger_modele('data/gram1.txt')
    parser.charger_evaluation('data/test1.txt')
    parser.evaluer(-1)


if __name__ == '__main__':
    tester_cky()
    #tester_evaluer_arbre()
    #tester_evaluation()
