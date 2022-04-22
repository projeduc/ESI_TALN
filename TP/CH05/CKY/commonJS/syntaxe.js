#!/usr/bin/env node

const { log } = require('console');
const fs = require('fs');
const path = require('path');
const _ = require('underscore');


function formater_liste(l){
  if (l == null) return 'None';
  let resultat = '(';
  for (const e of l){
    resultat += (e instanceof Array)? formater_liste(e): "'" + e.toString() + "'";
    resultat += ', ';
  }
  resultat = resultat.slice(0, -2);
  resultat += ')';
  return resultat;
}



// ======================================================
//                   Analyse CKY
// ======================================================

class CKY {
  constructor(gram, lex){
    //
    this.gram = gram;
    //
    this.lex = lex;
  }

  // TODO: Compléter cette méthode
  analyser(phrase){
    const T = [],
          N = phrase.length;
    let i,j,k,pos;
    for(i=0; i<N; i++){
      const Ti = [];
      T.push(Ti);
      for (j=0; j<N; j++) Ti.push([]);
      //T.push((new Array(N)).fill(new Array())); //crée la même référence pour les colonnes
      const mot = phrase[i];
      this.lex[mot].forEach(function(A){
        T[i][i].push([A, -1, -1, -1]);
      });
    }

    // Compléter ici : remplissage 
    return T;
  }

}

// ======================================================
//                Analyse syntaxique
// ======================================================


// TODO: compléter cette fonction
function evaluer_stricte(ref, sys){
  return 0
}


function construire(T, phrase, i, j, pos){
  let [A, k, iB, iC] = T[i][j][pos];
  A = A.toUpperCase();
  if (k>=0){
    const gauche = construire(T, phrase, i, k, iB);
    const droit = construire(T, phrase, k+1, j, iC);
    return [A, gauche, droit];
  }
  return [A, phrase[i]];
}

function parse_tuple(string){
  if (! string.startsWith('(')) return null;
  return eval(string.replace(/([^\s(),]+)/g, "'$1'").replace(/\(/g, '[').replace(/\)/g, ']'));
}

class Syntaxe {
  constructor(){
    this.eval = [];
  }

  _analyser(phrase){
    const T = this.modele.analyser(phrase),
          n = phrase.length - 1;
    let pos;
    
    for (pos=0; pos<T[0][n].length; pos++){
      if (T[0][n][pos][0] == 'S'){
        return construire(T, phrase, 0, n, pos);
      }
    }
    return null;
  }

  analyser(phrase){
    return this._analyser(phrase.trim().split(' '));
  }

  charger_modele(url){
    const f = fs.readFileSync(url, 'utf8');
    const lex = {}, gram=[];
    let i;
    f.split(/\r?\n/).forEach(function(l){
      l = l.trim();
      if ((l.length > 4) && (!l.startsWith('#'))){
        const info = l.split('	');
        if (info.length == 2){
          if (! (info[1] in lex)) lex[info[1]] = [];
          lex[info[1]].push(info[0]);
        } else if (info.length==3){
          gram.push([info[0], info[1], info[2]]);
        }
      }
    });
    this.modele = new CKY(gram, lex);
  }

  sauvegarder_modele(url){
    fs.writeFileSync(url, this.modele.exporter_json());
  }

  charger_evaluation(url){
    const f = fs.readFileSync(url, 'utf8');
    let i;
    f.split(/\r?\n/).forEach(function(l){
      l = l.trim();
      if ((l.length > 4) && (!l.startsWith('#'))){
        const info = l.split('	');
        this.eval.push([info[0], parse_tuple(info[1])]);
      }
    }, this);
  }

  evaluer(n){
    const S = (n == -1)? this.eval: _.sample(this.eval, n);
    let score = 0.0;
    S.forEach(function(test){
      console.log('=======================');
      console.log('phrase :', test[0]);
      console.log('arbre de référence :', formater_liste(test[1]));
      const arbre = this.analyser(test[0]);
      console.log('arbre du système :', formater_liste(arbre));
      const score_i = evaluer_stricte(test[1], arbre);
      console.log('score :', score_i);
      score += score_i;
    }, this);
    if (n == -1){ n = this.eval.length;}
    score = score/n;
    console.log('---------------------------------');
    console.log('score total : ', score);
    return score;
  }
}

// ======================================================
//        Génération du graphique (langage Dot)
// ======================================================

function generer_noeud(noeud){
  // Si le noeud est final,
  let id = -1;
  let pid = 0;
  let pile = [];
  let n = noeud;
  let res = '';
  while(pile.length > 0 || n != null){
    id += 1;
    if (n==null){
      [n, pid] = pile.pop();
    } else if (n.length < 3){
      res += 'N' + id +'[label="' + n[0] + "=" + n[1] + '" shape=box];\n';
      if (pid < id) res += 'N' + pid + ' ->  N' + id + ';\n';
      n = null;
    } else {
      res += 'N' + id + '[label="' + n[0] + '"];\n';
      if (pid < id) res += 'N' + pid + ' ->  N' + id + ';\n';
      //ce noeud sera un parent
      pid = id;
      //empiler la droite
      pile.push([n[2], pid]);
      //aller toujours vers la gauche
      n = n[1];
    }
  }
  return res;
}

function generer_graphviz(racine, url){
  let res = 'digraph Tree {\n';
  res += generer_noeud(racine);
  res += '}';
  fs.writeFileSync(url, res);
}

// ======================================================
//                       TESTES
// ======================================================

// le test du cours pour tester l'analyseur CKY
function tester_cky(){
  const parser = new Syntaxe();
  parser.charger_modele('data/gram1.txt');
  const phrase = "la petite forme une petite phrase";
  const resultat = "('S', ('NP', ('DET', 'la'), ('N', 'petite')), ('VP', ('V', 'forme'), ('NP', ('DET', 'une'), ('AP', ('ADJ', 'petite'), ('N', 'phrase')))))";
  const arbre = parser.analyser(phrase);
  console.log('résultat attendu : ', resultat);
  console.log('mon     résultat : ', formater_liste(arbre));
  generer_graphviz(arbre, 'arbre_js.gv')
}

// tester l'évaluation stricte
function tester_evaluer_arbre(){
  console.log('résultat attendu : 0, résultat trouvé : ', evaluer_stricte(null, ['A', 'd']));
  console.log('résultat attendu : 1, résultat trouvé : ', evaluer_stricte(null, null));
  console.log('résultat attendu : 0, résultat trouvé : ', evaluer_stricte(['A', 'a'], ['A', ['B', 'b'], ['C', 'c']]));
  console.log('résultat attendu : 0, résultat trouvé : ', evaluer_stricte(['A', ['B', 'b'], ['C', 'c']], ['A', 'a']));
  console.log('résultat attendu : 0, résultat trouvé : ', evaluer_stricte(['A', ['B', 'b'], ['C', 'c']], ['A', ['B', 'd'], ['C', 'c']]));
  console.log('résultat attendu : 1, résultat trouvé : ', evaluer_stricte(['A', ['B', 'b'], ['C', 'c']], ['A', ['B', 'b'], ['C', 'c']]));
}
    
// tester l'évaluation stricte
// Le résultat doit être 1
function tester_evaluation(){
  const parser = new Syntaxe();
  parser.charger_modele('data/gram1.txt');
  parser.charger_evaluation('data/test1.txt');
  parser.evaluer(-1);
}



// MAIN

tester_cky();
// tester_evaluer_arbre();
// tester_evaluation();
