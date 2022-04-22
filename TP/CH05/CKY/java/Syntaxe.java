import java.util.regex.*;
import java.io.*;
import java.util.*;
import java.lang.Math;
import java.util.stream.*;

public class Syntaxe {
	private CKY modele;
	private List<Tuple<String, Noeud>> eval = new ArrayList<>();

	public Noeud analyser(String[] phrase){
		List<List<List<CKYRef>>> T = this.modele.analyser(phrase);
		int N = phrase.length - 1;
		List<CKYRef> lastCell = T.get(0).get(N);
		for(int pos=0; pos<lastCell.size(); pos++){
			if (lastCell.get(pos).A.equals("S")){
				return Outils.construire(T, phrase, 0, N, pos);
			}
		}
		return null;
	}

	public Noeud analyser(String phrase){
		return analyser(phrase.split(" "));
	}

	public void charger_modele(String url){
		try {
			BufferedReader f = new BufferedReader(new FileReader(url));
			String l="";
			Map<String, List<String>> lex = new HashMap<>();
			List<Regle> gram = new ArrayList<>();
			while((l = f.readLine()) != null) {
				l = l.strip();
				if ((l.length() < 5)) continue;
				String[] info = l.split("	");
				if (info.length == 2){
					if (! lex.containsKey(info[1])) lex.put(info[1], new ArrayList<String>());
					lex.get(info[1]).add(info[0]);
				} else if (info.length==3){
					gram.add(new Regle(info[0], info[1], info[2]));
				}
			}
			f.close();
			this.modele = new CKY(gram, lex);
		} catch(IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}


	public void charger_evaluation(String url){
		try {
			BufferedReader f = new BufferedReader(new FileReader(url));
			String l;
			while((l = f.readLine()) != null) {
				l = l.strip();
				if ((l.length() < 5)) continue;
				String[] info = l.split("	");
				this.eval.add(new Tuple<String, Noeud>(info[0], Outils.parse_tuple(info[1])));
			}
			f.close();
		} catch(IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public void evaluer(int n){
		if (n==-1) n = this.eval.size();
		else Collections.shuffle(this.eval);

		double score = 0.0;
		for (int i=0; i<n; i++){
			Tuple<String, Noeud> test = this.eval.get(i);
			System.out.println("=======================");
			System.out.println("phrase : " + test.k);
			System.out.println("arbre de référence : " + test.v);
			Noeud arbre = analyser(test.k);
			System.out.println("arbre du système   : " + arbre);
			int score_i = Outils.evaluer_stricte(test.v, arbre);
            System.out.println("score :" + score_i);
            score += score_i;
		}

		score /= n;
        System.out.println("---------------------------------");
        System.out.println("score total : " + score);
  }


	// ======================================================
	//                       TESTES
	// ======================================================

	public static void tester_cky(){
		Syntaxe parser = new Syntaxe();
		parser.charger_modele("data/gram1.txt");
		String phrase = "la petite forme une petite phrase";
		Noeud arbre = parser.analyser(phrase);
		String resultat = "('S', ('NP', ('DET', 'la'), ('N', 'petite')), ('VP', ('V', 'forme'), ('NP', ('DET', 'une'), ('AP', ('ADJ', 'petite'), ('N', 'phrase')))))";
    	arbre = parser.analyser(phrase);
    	System.out.println("résultat attendu : " + resultat);
    	System.out.println("mon     résultat : " + arbre);
		generer_graphviz(arbre, "arbre_java.gv");
	}

	public static void tester_evaluer_arbre(){
		List<Integer> resultat = new ArrayList<>();
		List<Noeud> test1 = new ArrayList<>();
		List<Noeud> test2 = new ArrayList<>();

		test1.add(null);
		test2.add(new Noeud("A", new Noeud("d", null, null), null));
		resultat.add(0);

		test1.add(null);
		test2.add(null);
		resultat.add(1);

		test1.add(new Noeud("A", new Noeud("a", null, null), null));
		test2.add(new Noeud("A", new Noeud("B", new Noeud("b", null, null), null), new Noeud("C", new Noeud("c", null, null), null)));
		resultat.add(0);

		test1.add(new Noeud("A", new Noeud("B", new Noeud("b", null, null), null), new Noeud("C", new Noeud("c", null, null), null)));
		test2.add(new Noeud("A", new Noeud("a", null, null), null));
		resultat.add(0);

		test1.add(new Noeud("A", new Noeud("B", new Noeud("b", null, null), null), new Noeud("C", new Noeud("c", null, null), null)));
		test2.add(new Noeud("A", new Noeud("B", new Noeud("d", null, null), null), new Noeud("C", new Noeud("c", null, null), null)));
		resultat.add(0);

		test1.add(new Noeud("A", new Noeud("B", new Noeud("b", null, null), null), new Noeud("C", new Noeud("c", null, null), null)));
		test2.add(new Noeud("A", new Noeud("B", new Noeud("b", null, null), null), new Noeud("C", new Noeud("c", null, null), null)));
		resultat.add(1);

		for (int i=0; i<resultat.size(); i++){
			System.out.print("résultat attendu : " + resultat.get(i));
			System.out.println(", résultat trouvé : " + evaluer_stricte(test1.get(i), test2.get(i)));
		}

	}

	public static void tester_evaluation(){
		Syntaxe parser = new Syntaxe();
		parser.charger_modele("data/gram1.txt");
		parser.charger_evaluation("data/test1.txt");
		parser.evaluer(-1);
	}

	public static void main(String[] args) {
		//tester_cky();
		//tester_evaluer_arbre();
		tester_evaluation();
	}
}

class CKY {
	private List<Regle> gram;
	private Map<String, List<String>> lex;

	public CKY(List<Regle> gram, Map<String, List<String>> lex){
		this.gram = gram;
		this.lex = lex;
	}

	//TODO: Compléter cette méthode
	public List<List<List<CKYRef>>> analyser(String[] phrase){
		int N = phrase.length;
		List<List<List<CKYRef>>> T = new ArrayList<>();
		for(int i=0; i<N; i++){
			List<List<CKYRef>> Ti = new ArrayList<>();
			T.add(Ti);
			for(int j=0; j<N; j++) Ti.add(new ArrayList<CKYRef>());
			String mot = phrase[i];
			for (String A : this.lex.get(mot)){
				T.get(i).get(i).add(new CKYRef(A, -1, -1, -1));
			}
		}

		// Compléter ici : remplissage 
		return T;
	}

}

class Outils {

	//TODO: compléter cette fonction
	public static int evaluer_stricte(Noeud ref, Noeud sys){
    	return 0;
	}

	public static Noeud construire(List<List<List<CKYRef>>> T, String[] phrase, int i, int j, int pos){
		CKYRef ref = T.get(i).get(j).get(pos);
		String A = ref.A.toUpperCase();
		if (ref.k >= 0){
			Noeud gauche = construire(T, phrase, i, ref.k, ref.iB);
			Noeud droit = construire(T, phrase, ref.k + 1, j, ref.iC);
			return new Noeud(A, gauche, droit);
		}
		return new Noeud(A, new Noeud(phrase[i], null, null), null);
	}

	public static String generer_noeud(Noeud noeud){
		int id = -1;
		int pid = 0;
		Deque<NoeudPere> pile = new ArrayDeque<>();
		Noeud n = noeud;
		String res = "";
		while(pile.size() > 0 || n != null){
			id += 1;
			if (n==null){
				NoeudPere npid = pile.pop();
				n = npid.n;
				pid = npid.pid;
			} else if (n.gauche == null){
				res += "N" + id +"[label=\"" + n.val + "\" shape=box];\n";
				if (pid < id) res += "N" + pid + " ->  N" + id + ";\n";
				n = null;
			} else {
				res += "N" + id + "[label=\"" + n.val + "\"];\n";
				if (pid < id) res += "N" + pid + " ->  N" + id + ";\n";
				//ce noeud sera un parent
				pid = id;
				//empiler la droite
				pile.push(new NoeudPere(n.droit, pid));
				//aller toujours vers la gauche
				n = n.gauche;
			}
		}
		return res;
	}

	public static void generer_graphviz(Noeud racine, String url){
		String res = "digraph Tree {\n";
		res += generer_noeud(racine);
		res += "}";
		try {
			BufferedWriter f = new BufferedWriter(new FileWriter(url));
			f.write(res);
			f.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	private static int trouver_par_ferme(String string){
		LinkedList<Character> pile = new LinkedList<>();
		for(int i = 0;i<string.length();i++){
			char c = string.charAt(i);
			if(c == '(') pile.push(c);
			else if (c == ')') pile.pop();

			if(pile.size() == 0) return i;
		}
		return -1;
	}

	public static Noeud parse_tuple(String string){
		string = string.replace(" ", "");
		if (! string.startsWith("(")) return null;
		string = string.substring(1, string.length()-1);
		int idxVirgule = string.indexOf(",");
		if (idxVirgule == -1) return null;
		String val = string.substring(0, idxVirgule);
		string = string.substring(idxVirgule+1);
		if (! string.startsWith("(")) return new Noeud(val, new Noeud(string, null, null), null);
		
		
		int idxParFerm = trouver_par_ferme(string);
		if (idxParFerm == -1) return null;
		Noeud gauche = parse_tuple(string.substring(0, idxParFerm+1));
		Noeud droit = null;
		if (idxParFerm < string.length()) droit = parse_tuple(string.substring(idxParFerm+2));

		return new Noeud(val, gauche, droit);
	}

}

class CKYRef {
	public final String A;
	public final int k;
	public final int iB;
	public final int iC;
	public CKYRef(String A, int k, int iB, int iC){
		this.A = A;
		this.k = k;
		this.iB = iB;
		this.iC = iC;
	}

	public String toString(){
		return "(" + A + ", " + k + ", " + iB + ", " + iC + ")";
	}
}

class Regle {
	public final String A;
	public final String B;
	public final String C;
	public Regle(String a, String b, String c) {
		this.A = a;
		this.B = b;
		this.C = c;
	}

	public String toString(){
		return A + " -> " + B + " " + C;
	}
}

class Noeud {
	public final String val;
	public final Noeud gauche;
	public final Noeud droit;
	public Noeud(String val, Noeud gauche, Noeud droit) {
		this.val = val;
		this.gauche = gauche;
		this.droit = droit;
	}

	public int len(){
		if (gauche != null && droit != null) return 3;
		if (gauche != null) return 2;
		if (gauche == null && droit == null) return 1;
		return 0;
	}

	public String toString(){
		if (gauche == null && droit == null) return "'" + val + "'";

		String res = "('" + val + "'";
		res += ", " + gauche;
		if (droit != null) res += ", " + droit;
		res += ")";
		return res;
	}
}

class NoeudPere {
	public final Noeud n;
	public final int pid;
	public NoeudPere(Noeud n, int pid) {
		this.n = n;
		this.pid = pid;
	}
}

class Tuple<K, V> {
	public final K k;
	public final V v;
	public Tuple(K k, V v) {
		this.k = k;
		this.v = v;
	}

	public String toString(){
		return "(" + k + ", " + v + ")";
	}
}