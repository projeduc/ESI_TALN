#!/usr/bin/env node

const fs = require('fs');
const path = require('path');
const repTab = [/\$0/, /\$1/, /\$2/, /\$3/, /\$4/, /\$5/,
                /\$6/, /\$7/, /\$8/, /\$9/];

// TODO vous pouvez définir d'autres RegEx pour les trois types
const mails_remp = [
    {reg: /(\w+)@(\w+)\.(\w+)/g, pat: '$1@$2.$3'}
],
socials_remp = [
    {reg: /https:\/\/(\w+)\/([^/]+)/g, pat: '$1:$2'}
],
tels_remp = [
    {reg: /(\d\d)-(\d\d)-(\d\d)-(\d\d)/g, pat: '(0$1) $2 $3 $4'}
];

// ============================================================
// ne modifiez pas ça
const regs = {
    'mails' : mails_remp,
    'socials' : socials_remp,
    'tels' : tels_remp
}

/* TODO si vous avez bien défini les expressions régulières,
 * vous n'allez pas besoin de modifier cette fonction
 *
 * Sinon, vous pouvez modifier la fonction, si ça peut vous aidera
 */
function traiter_fichier(url){
    const resultat = {
        'mails' : [],
        'socials' : [],
        'tels' : []
    };

    const f = fs.readFileSync(url, "utf8");
    for (type in regs) {
        const regtab = regs[type];
        let i;
        for (i in regtab) {
            let ms;
            while ((m = regtab[i].reg.exec(f)) !== null){
                let contenu = regtab[i].pat;
                let j;
                for (j = 1; j < m.length; j++){
                    //console.log(m[j]);
                    contenu = contenu.replace(repTab[j], m[j]);
                }
                resultat[type].push(contenu);
            }
        }
    }
    //f.close();
    return resultat;
}

/*
 * Calculer le nombre d'occurrences de chaque élément dans une liste
 * Entrée : une liste
 * Sortie : un dictionnaire (élément --> nombre d'occurrences)
 */
function traiter_stats(lst){
    const stats = {};
    let i;
    for (i in lst){
        const e = lst[i];
        if (! (e in stats))
            stats[e] = 0;
        stats[e] += 1;
    }
    return stats;
}

/*
 * Parcourir les fichier d'un dossier et pour chaque fichier extraire les contactes
 * Entrée : le chemin vers le dossier
 * Sortie : un dictionnaire (nom du fichier --> contactes)
 * Une contacte c'est un autre dictionnaire (type --> stats)
 */
function traiter_dossier(url) {
    const contacts = {};
    const nomfs = fs.readdirSync(url);
    nomfs.forEach(function(nomf, index, array){
        const m = /^([^.].*)\.html$/.exec(nomf);
        if (m) {
            const titre = m[1];
            url_f = path.join(url, nomf);
            resultat = traiter_fichier(url_f);
            contact = {
                'mails' : traiter_stats(resultat['mails']),
                'socials' : traiter_stats(resultat['socials']),
                'tels' : traiter_stats(resultat['tels'])
            }
            contacts[titre] = contact;
        }
    });
    return contacts;
}

function traiter_reference(url){
    const contacts = {};
    const f = fs.readFileSync(url, 'utf8');
    f.split('\n').forEach(function(l, index, array){
        info = l.split('	');
        if (info.length > 3){
            if (! contacts[info[0]]){
                contacts[info[0]] = {
                    'mails' : {},
                    'socials' : {},
                    'tels' : {}
                };
            }
            contacts[info[0]][info[1]][info[2]] = info[3] * 1;
        }
    });
    return contacts;
}

function comparer(sys_contacts, ref_contacts){
    let INT = 0, // taille ref intersection sys
        SYS = 0, // taille sys
        REF = 0; // taille ref
    const resultat = {};
    for (fichier in ref_contacts){
        resultat[fichier] = {};
        const ref_types = ref_contacts[fichier];
        const sys_types = (fichier in sys_contacts)? sys_contacts[fichier]: null;
        for (type in ref_types){
            const res_elements = resultat[fichier][type] = {};
            for (element in ref_types[type]){
                const ref_nbr = ref_types[type][element];
                let sys_nbr = 0;
                if ((sys_types) && (element in sys_types[type])){
                    sys_nbr = sys_types[type][element];
                }
                res_elements[element] = 'sys(' + sys_nbr + '), ref(' + ref_nbr + ')';
                SYS += sys_nbr;
                REF += ref_nbr;
                INT += (sys_nbr < ref_nbr)? sys_nbr : ref_nbr;
            }
            if (sys_types){
                for (element in sys_types[type]){
                    if (! res_elements[element]){
                        sys_nbr = sys_types[type][element];
                        res_elements[element] = 'sys(' + sys_nbr + '), ref(0)';
                        SYS += sys_types[type][element];
                    }
                }
            }
        }
    }
    R = INT / REF;
    P = (SYS==0)? 0.0 : INT / SYS;
    F1 = (R+P == 0)? 0.0 : 2 * P * R / (P + R);
    return [resultat, R, P, F1];
}

// Affichage des statistiques
function affichage(contacts) {
    for (fichier in contacts) {
        console.log('========== ', fichier, ' ==========')
        stats = contacts[fichier]
        for (type in stats) {
            console.log('------> ', type)
            stats_type = stats[type]
            for (element in stats_type) {
                console.log('         ', element, ' : ', stats_type[element])
            }
        }
    }
}


const args = process.argv;
let url = './';
if (args.length > 2) url = args[2];
sys_contacts = traiter_dossier(url);
ref_contacts = traiter_reference(path.join(url, 'ref.txt'));
res = comparer(sys_contacts, ref_contacts);
affichage(res[0]);
console.log('---------------------------------------------------');
console.log('R =', res[1], ', P =', res[2], ', F1 =', res[3]);
