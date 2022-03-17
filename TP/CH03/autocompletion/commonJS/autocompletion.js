#!/usr/bin/env node

const fs = require('fs');
const path = require('path');
const _ = require('underscore');

class LaplaceBiGram {
  constructor(){
    this.uni_grams = {'<s>': 0};
    this.bi_grams = {};
  }

  // TODO : compléter cette méthode
  entrainer(data){
  }

  // TODO : compléter cette méthode
  noter(past, current){
    return 0.0;
  }

  // TODO : compléter cette méthode
  estimer(mots){
    const mots_scores = {};
    //# ajouter des tuples (mot --> score) à ce dictionnaire
    return Object.entries(mots_scores).sort(([,x],[,y]) => y-x);
                 //.reduce((r, [k, v]) => ({ ...r, [k]: v }), {});
  }

  exporter_json(){
    return JSON.stringify(this);
  }

  importer_json(data){
    for (cle in data){
      this[cle] = data[cle];
    }

  }
}


class Autocompletion {
  constructor(){
    this.modele = new LaplaceBiGram();
    this.eval = [];
  }

  entrainer(url){
    const f = fs.readFileSync(url, 'utf8');
    const data = [];
    f.split(/\r?\n/).forEach(function(l){
      const phrase = l.trim().toLowerCase().split(' ');
      if (phrase.length > 0) data.push(phrase);
    });
    this.modele.entrainer(data);
  }

  estimer(phrase, nbr){
    const mots_scores = this.modele.estimer(phrase.trim().toLowerCase().split(' '));
    return mots_scores.slice(0, nbr);
  }

  charger_modele(url){
    const f = fs.readFileSync(url, 'utf8');
    const data = JSON.parse(f);
    this.modele.importer_json(data);
  }

  sauvegarder_modele(url){
    fs.writeFileSync(url, this.modele.exporter_json());
  }

  charger_evaluation(url){
    const f = fs.readFileSync(url, 'utf8');
    f.split(/\r?\n/).forEach(function(l){
      const info = l.toLowerCase().split('	');
      if (info.length > 1) this.eval.push(info);
    }, this);
  }

  evaluer(n, m){//Mean reciprocal rank
    let S = this.eval;
    if (n == -1) n = S.length;
    else S = _.sample(this.eval, n);
    let score = 0.0;
    let j = 0;
    for (j in S){
      const test = S[j];
      console.log('p(', test[1], '|', test[0], ')');
      const res = this.estimer(test[0], m);
      console.log('found:', res);
      const i = res.map(function(e){return e[0]}).indexOf(test[1]) + 1;
      if (i > 0) score += 1/i;
    };
    score = score/n;
    console.log('Score = ', score);
    return score;
  }


}


const program = new Autocompletion();
program.entrainer('data/algerie_train.txt');
program.sauvegarder_modele('./t.json');
// const phrase = "L' Algérie faisant partie du";
// const res = program.estimer(phrase, 10);
// console.log(res);
program.charger_evaluation('data/algerie_test.txt');
// 80 exemplaires ; 10 résultats possibles
mrr = program.evaluer(80, 10);
