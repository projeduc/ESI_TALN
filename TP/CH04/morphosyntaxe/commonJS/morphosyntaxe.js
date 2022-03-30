#!/usr/bin/env node

const fs = require('fs');
const path = require('path');
const _ = require('underscore');

function init_inc(map, cle, init_val=1.0, inc_val=1.0){
  if (cle in map) map[cle] += inc_val;
  else map[cle] = init_val;
}

class HMM {
  constructor(tags){
    // Tag_i --> tag_i+1 --> prob
    this.transition = {}; // probabilités de transition
    // Tag --> mot --> prob
    this.emission = {}; // probabilité d'émission
    // Tag --> prob
    this.init = {}; // probabilité initiale des états
    this.tags = tags;
    // score des mots non trouvées dans l'emission
    // unk_score = 1/|V|
    this.unk_score = 0.0;
    let i, j;
    for (i in tags){
      const tag = tags[i];
      this.transition[tag] = {}
      this.emission[tag] = {}
      this.init[tag] = 0.0
      for (j in tags){
        const tag_suiv = tags[j];
        this.transition[tag][tag_suiv] = 0.0;
      }
    }
  }

  entrainer(data){
    const tags_freq = {};
    const vocab = new Set();
    //calcul des fréquences
    let i, j;
    for (i=0; i<data.length; i++){
      const phrase = data[i];
      let [mot_prec, tag_prec] = phrase[0];
      vocab.add(mot_prec);
      init_inc(this.init, tag_prec);
      init_inc(tags_freq, tag_prec);
      if (!(tag_prec in this.emission)) this.emission[tag_prec] = {};
      init_inc(this.emission[tag_prec], mot_prec);
      for (j=1; j< phrase.length; j++){
        const [mot, tag] = phrase[j];
        vocab.add(mot);
        // transition tag_prec --> tag
        init_inc(this.transition[tag_prec], tag);
        // émission tag --> mot
        init_inc(this.emission[tag], mot);
        init_inc(tags_freq, tag);
        [mot_prec, tag_prec] = [mot, tag];
      }
    }

    const V = vocab.size;
    this.unk_score = 1.0/V;
    const Vt = this.tags.length;
    const nbr_phrases = data.length;
    for (i=0; i<Vt; i++){
      const tag = this.tags[i];
      this.init[tag] = (this.init[tag]+1)/(nbr_phrases+Vt);
      const nbr_tag = tags_freq[tag];
      const emission_tag = this.emission[tag];
      for (const mot in emission_tag){
        emission_tag[mot] = (emission_tag[mot]+1.0)/(nbr_tag + V);
      }
      const transition_tag = this.transition[tag];
      for (j=0; j<Vt; j++){
        const tag_suiv = this.tags[j];
        transition_tag[tag_suiv] = (transition_tag[tag_suiv]+1.0)/(nbr_tag+Vt);
      }
    }

  }

  //TODO: compléter cette méthode
  noter(past_pos, current_pos, mot){
    return Math.log(1);
  }

  estimer_force_brute(phrase){
    let tags_max = [];
    let score_max = Number.NEGATIVE_INFINITY;
    const pile = [['', 0, 0.0]];
    const N = this.tags.length,
          T = phrase.length;

    while (pile.length > 0){
      let i = pile.length - 1;
      let [past_tag, t, score] = pile.pop();
      pile.push([past_tag, t+1, score]);
      // forward
      while(i < T){
        const tag = this.tags[t];
        const mot = phrase[i];
        score += this.noter(past_tag, tag, mot);
        pile.push([tag, 1, score]);
        i += 1;
        t =  0;
        past_tag = tag;
      }

      if (score_max < score){
        score_max = score;
        tags_max = pile.map(e => e[0]);
      }

      // backward
      pile.pop(); //dépiler le dernier élément
      while((pile.length > 0) && (pile[pile.length-1][1] == N)) pile.pop();
    }

    return tags_max.slice(1, tags_max.length);
  }

  estimer_gourmand(phrase){
    const tags = [];
    let past_tag = '';
    let i,j;
    for (i in phrase){
      const mot = phrase[i];
      let max_score = Number.NEGATIVE_INFINITY,
          max_tag = '';
      for (j in this.tags){
        const tag = this.tags[j];
        const score = this.noter(past_tag, tag, mot);
        if (score > max_score){
          max_score = score;
          max_tag = tag;
        }
      }
      tags.push(max_tag);
      past_tag = max_tag;
    }
    return tags;
  }

  //TODO: compléter cette méthode : vous devez suivre la même structure définie ici
  estimer_viterbi(phrase){
    const tags_list = this.tags,
          N = tags_list.length,
          T = phrase.length;
    const viterbi = Array(N).fill().map(() => Array(T).fill(Number.NEGATIVE_INFINITY)),
        backpointer = Array(N).fill().map(() => Array(T).fill(0));

    const tags = [];

    return tags;
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


class MorphoSyntaxe {
  constructor(){
    this.eval = [];
  }

  entrainer(url){
    const f = fs.readFileSync(url, 'utf8');
    const data = [],
          tags_list = new Set();
    let i;
    f.split('\n').forEach(function(l){
      const phrase = l.trim().toLowerCase().replace('\\/', '@@').split(' ');
      if (phrase.length > 0){
        const p = [];
        for (i in phrase){
          const [mot, tag] = phrase[i].split('/');
          p.push([mot.replace('@@', '/'), tag]);
          tags_list.add(tag);
        }
        data.push(p);
      }
    });
    this.modele = new HMM([...tags_list]);
    this.modele.entrainer(data);
  }

  _estimer(phrase, opt='forcebrute'){
    let tags = [];
    switch(opt){
      case 'viterbi':
        tags = this.modele.estimer_viterbi(phrase);
        break;
      case 'gourmand' :
        tags = this.modele.estimer_gourmand(phrase);
        break;
      default: 
        tags = this.modele.estimer_force_brute(phrase);
    }
    return tags;
    //return phrase.map((_, i) => [phrase[i],tags[i]]);
  }

  estimer(phrase, opt='forcebrute'){
    return this._estimer(phrase.trim().toLowerCase().split(' '), opt);
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
    let i;
    f.split(/\n/).forEach(function(l){
      const mottags = l.trim().toLowerCase().replace('\\/', '@@').split(' ');
      if (mottags.length > 1){
        const mots = [], tags = [];
        for (i in mottags){
          let [mot, tag] = mottags[i].split("/");
          mots.push(mot.replace('@@', '/'));
          tags.push(tag);
        }
        this.eval.push([mots, tags]);
      }
    }, this);
  }

  evaluer_phrase(sysTags, refTags){
    let INT = 0.0;
    let i;
    for(i=0; i<sysTags.length; i++){
      if (sysTags[i] == refTags[i]) INT++;
    }
    return INT/refTags.length;
  }

  evaluer(n){
    const S = (n == -1)? this.eval: _.sample(this.eval, n);

    let score_gourmand = 0.0,
        score_viterbi = 0.0,
        score_force_brute = 0.0;
    let sysTags, score_i;

    S.forEach(function(test){
      console.log('=======================');
      console.log('phrase :', test[0]);
      console.log('tags de référence :', test[1]);

      // sysTags = this._estimer(test[0]);
      // console.log('brute force :', sysTags);
      // score_i = this.evaluer_phrase(sysTags, test[1]);
      // console.log('score :', score_i);
      // score_force_brute += score_i;

      sysTags = this._estimer(test[0], 'viterbi');
      console.log('viterbi :', sysTags);
      score_i = this.evaluer_phrase(sysTags, test[1]);
      console.log('score :', score_i);
      score_viterbi += score_i;

      sysTags = this._estimer(test[0], 'gourmand');
      console.log('gourmand :', sysTags);
      score_i = this.evaluer_phrase(sysTags, test[1]);
      console.log('score :', score_i);
      score_gourmand += score_i;

    }, this);

    if (n == -1){ n = this.eval.length;}

    score_gourmand /= n;
    score_viterbi /= n;
    score_force_brute /= n;
    console.log('----------- Scores totaux -------------');
    console.log('score gourmand : ', score_gourmand);
    console.log('score viterbi : ', score_viterbi);
    console.log('score force brute : ', score_force_brute);

  }

}

function tester_noter(){
  const annotateur = new MorphoSyntaxe();
  annotateur.entrainer('./data/test.txt');
  const phrase = 'il peut aider';
  const tags = ['det', 'verb', 'verb'];
  const scores = [-3.465735902799726, -3.401197381662155, -3.3141860046725258];
  const mots = phrase.toLowerCase().split(' ');

  past_tag = '';
  let i;
  for(i=0; i< mots.length; i++){
    let mot = mots[i];
    let tag = tags[i];
    let score = scores[i];
    console.log('mot=', mot, ', tag=', tag, ', score=', score, ', mon score =', annotateur.modele.noter(past_tag, tag, mot));
    past_tag = tag;
  }
}


function tester_viterbi(){
  const annotateur = new MorphoSyntaxe();
  annotateur.entrainer('./data/univ_train.txt');
  const phrase = 'this has some logic .';
  console.log('viterbi doit être comme force brute');
  console.log('force brute : ', annotateur.estimer(phrase));
  console.log('gourmand : ', annotateur.estimer(phrase, opt='gourmand'));
  console.log('viterbi : ', annotateur.estimer(phrase, opt='viterbi'));
}

function evaluation(){
  const annotateur = new MorphoSyntaxe();
  annotateur.entrainer('./data/univ_train.txt');
  annotateur.charger_evaluation('./data/univ_test.txt');
  annotateur.evaluer(-1);
}

//tester_noter();
//tester_viterbi();
evaluation();