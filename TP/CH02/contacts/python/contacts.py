import re
import os
import sys

# TODO vous pouvez définir d'autres RegEx pour les trois types
mails_remp = [
    (re.compile(u'(^|[^\w])(\w[\w.-]*)@(\w[\w.-]*)\.(\w+)'), '$2@$3.$4')
]

socials_remp = [
    (re.compile(u'(facebook|twitter|instagram)\.com/([\w.-]+)(/?[^\w./?-])'), '$1:$2'),
    (re.compile(u'(youtube)\.com/channel/([\w.-]+)'), '$1:$2'),
    (re.compile(u'(linkedin)\.com/company/([\w.-]+)'), '$1:$2')
]

tels_remp = [
    (re.compile(u'(^|[^\d\s]\s*)\(?0\s*(\d\d)\)?\s*[- \.]?\s*(\d\d)\s*[- \.]?\s*(\d\d)\s*[- \.]?\s*(\d\d)'), '(0$2) $3 $4 $5'),

    (re.compile(u'([^\d\s])\s*\(?(0\d)\)? *[- \.]? *(\d\d) *[- \.]? *(\d\d)(\d) *[- \.]? *(\d)(\d\d)'), '($2$3) $4 $5$6 $7'),

    (re.compile(u'([^\d\s])\s*(\+?|00)\s*\(?213\)?\s*\(?(\d\d\d)\)?\s*[- \.]?\s*[- \.]?\s*(\d\d)(\d)\s*[- \.]?\s*(\d)(\d\d)\D'), '(0$3) $4 $5$6 $7'),

    (re.compile(u'([^\d\s])\s*(\+?|00)\s*\(?213\)?\s*\(?\s*0?\s*\)?\s*(\d\d)\s*[-\.]?\s*(\d\d)\s*[-\.]?\s*(\d\d)\s*[-\.]?\s*(\d\d)\D'), '(0$3) $4 $5 $6'),

    (re.compile(u'([^\d\s])\s*(\+?|00)\s*\(?\+?213\)?\s*\(?\s*0?\s*\)?\s*(\d\d)\s*[-\.]?\s*(\d\d)\s*[-\.]?\s*(\d\d)\s*[-.]?\s*(\d\d)\s*(/| ou | à )[\s ]*(\d\d)(\s*[\S\D])'), '(0$3) $4 $5 $8'),
    (re.compile(u'([^\d\s])\s*(\+?|00)\s*\(?\+?213\)?\s*\(?\s*0?\s*\)?\s*(\d\d)\s*[-\.]?\s*(\d\d)\s*[-\.]?\s*(\d\d)\s*[-\.]?\s*(\d\d) ou (\d\d) ou (\d\d)(\s*[\S\D])'), '(0$3) $4 $5 $8'),
    (re.compile(u'([^\d\s])\s*(\+?|00)\s*\(?\+?213\)?\s*\(?\s*0?\s*\)?\s*(\d\d)\s*[-\.]?\s*(\d\d)\s*[-\.]?\s*(\d\d)\s*[-\.]?\s*(\d\d) ou (\d\d) ou (\d\d) ou (\d\d)(\s*[\S\D])'), '(0$3) $4 $5 $9'),

    (re.compile(u'([^\d\s])\s*(\+?|00)\s*\(?213\)?\s+(\d\d)\s*[- \.]?\s*(\d\d)(\d)\s*[- \.]?\s*(\d)(\d\d)\D'), '(0$3) $4 $5$6 $7'),
]

# ============================================================
# ne modifiez pas ça
regs = {
    'mails' : mails_remp,
    'socials' : socials_remp,
    'tels' : tels_remp
}

# TODO si vous avez bien défini les expressions régulières,
# vous n'allez pas besoin de modifier cette fonction
#
# Sinon, vous pouvez modifier la fonction, si ça peut vous aidera
def traiter_fichier(url):
    resultat = {
        'mails' : [],
        'socials' : [],
        'tels' : []
    }

    f = open(url, 'r')
    for l in f: # la lecture ligne par ligne
        for type in regs:
            for reg, patron in regs[type] :
                ms = reg.findall(l)
                for m in ms:
                    contenu = patron
                    for j in range(len(m)):
                        contenu = contenu.replace('$' + str(j+1), m[j])
                    resultat[type].append(contenu)
    f.close()
    return resultat

# Calculer le nombre d'occurrences de chaque élément dans une liste
# Entrée : une liste
# Sortie : un dictionnaire (élément --> nombre d'occurrences)
def traiter_stats(lst):
    stats = {}
    for e in lst:
        if not e in stats:
            stats[e] = 0
        stats[e] += 1
    return stats


# Parcourir les fichier d'un dossier et pour chaque fichier extraire les contactes
# Entrée : le chemin vers le dossier
# Sortie : un dictionnaire (nom du fichier --> contactes)
# Une contacte c'est un autre dictionnaire (type --> stats)
def traiter_dossier(url):
    contacts = {}
    for nomf in os.listdir(url):
        m = re.match('^([^.].*)\.html$', nomf)
        if m :
            titre = m[1]
        else:
            continue
        url_f = os.path.join(url, nomf)
        resultat = traiter_fichier(url_f)
        contact = {
            'mails' : traiter_stats(resultat['mails']),
            'socials' : traiter_stats(resultat['socials']),
            'tels' : traiter_stats(resultat['tels'])
        }
        contacts[titre] = contact
    return contacts

def traiter_reference(url):
    contacts = {}
    f = open(url, 'r')
    for l in f: # la lecture ligne par ligne
        info = l.split("	")
        if len(info) < 4 :
            continue
        if not info[0] in contacts:
            contacts[info[0]] = {
                'mails' : {},
                'socials' : {},
                'tels' : {}
            }
        contacts[info[0]][info[1]][info[2]] = int(info[3])
    f.close()
    return contacts

def comparer(sys_contacts, ref_contacts):
    INT = 0 # taille ref intersection sys
    SYS = 0 # taille sys
    REF = 0 # taille ref
    resultat = {}
    for fichier in ref_contacts:
        resultat[fichier] = {}

        ref_types = ref_contacts[fichier]
        if fichier in sys_contacts:
            sys_types = sys_contacts[fichier]
        else:
            sys_types = None

        for type in ['mails', 'socials', 'tels']:
            res_elements = resultat[fichier][type] = {}
            for element in ref_types[type]:
                ref_nbr = ref_types[type][element]
                sys_nbr = 0
                if (sys_types != None) and (element in sys_types[type]):
                    sys_nbr = sys_types[type][element]
                res_elements[element] = 'sys(' + str(sys_nbr) + '), ref(' + str(ref_nbr) + ')'
                SYS += sys_nbr
                REF += ref_nbr
                INT += min(sys_nbr, ref_nbr)
            if (sys_types):
                for element in sys_types[type]:
                    if not element in res_elements:
                        sys_nbr = sys_types[type][element]
                        res_elements[element] = 'sys(' + str(sys_nbr) + '), ref(0)'
                        SYS += sys_types[type][element]
    R = INT / REF
    P = 0.0 if SYS == 0 else INT / SYS
    F1 = 0.0 if R + P == 0 else 2 * P * R / (P + R)
    return resultat, R, P, F1

# Affichage des statistiques
def affichage(contacts):
    for fichier in contacts:
        print('========== ', fichier, ' ==========')
        stats = contacts[fichier]
        for type in stats:
            print('------> ', type)
            stats_type = stats[type]
            for element in stats_type:
                print('         ', element, ' : ', stats_type[element])

if __name__ == '__main__':
    url = './'
    if len(sys.argv) > 1:
        url = sys.argv[1]
    sys_contacts = traiter_dossier(url)
    ref_contacts = traiter_reference(os.path.join(url, 'ref.txt'))
    comp, R, P, F1 = comparer(sys_contacts, ref_contacts)
    affichage(comp)
    print('---------------------------------------------------')
    print('R =', R, ', P =', P, ', F1 =', F1)
