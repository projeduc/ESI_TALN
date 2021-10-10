import java.util.regex.*;
import java.io.*;
import java.util.*;

public class Contacts {
	private static Map<String, Map<Pattern, String>> regs = genererRegs();

	private static Map<String, Map<Pattern, String>> genererRegs(){
		Map<Pattern, String> mails_remp = new HashMap<>();
		mails_remp.put(Pattern.compile("(\\w+)@(\\w+)\\.(\\w+)"), "$1@$2.$3");
		//TODO ajouter d'autres regex de mail

		Map<Pattern, String> sites_remp = new HashMap<>();
		sites_remp.put(Pattern.compile("(https)://(\\w+)\\.(\\w+)"), "$1://$2.$3");
		//TODO ajouter d'autres regex de sites

		Map<Pattern, String> tels_remp = new HashMap<>();
		tels_remp.put(Pattern.compile("(\\d\\d)-(\\d\\d)-(\\d\\d)-(\\d\\d)"), "(0$1) $2 $3 $4");
		//TODO ajouter d'autres regex de tels

		//ne modifiez pas ça
		Map<String, Map<Pattern, String>> regs = new HashMap<>();
		regs.put("mails", mails_remp);
		regs.put("sites", sites_remp);
		regs.put("tels", tels_remp);

		return regs;
	}

	public static Map<String, List<String>> traiterFichier(String url) {
		Map<String, List<String>> resultat = new HashMap<>();
		resultat.put("mails", new ArrayList());
		resultat.put("sites", new ArrayList());
		resultat.put("tels", new ArrayList());

		Matcher m;
		try {
			BufferedReader f = new BufferedReader(new FileReader(url));
			String l;
			while((l = f.readLine()) != null) {
				for (String type: regs.keySet()) {
					Map<Pattern, String> rep = regs.get(type);
					for(Pattern reg: rep.keySet()){
						m = reg.matcher(l);
						while(m.find()) {
							//String contenu = String.format(rep.get(reg), m.);
							String contenu = rep.get(reg);
							for(int j=1; j <= m.groupCount(); j++) {
								contenu = contenu.replace("$" + j, m.group(j));
							}
							resultat.get(type).add(contenu);
						}
					}

				}
			}
			f.close();
		} catch(IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		return resultat;
	}
	
	public static  Map<String, Integer> traiterStats(List<String> lst){
		Map<String, Integer> stats = new HashMap<>();
		for (String e : lst) {
			if (! stats.containsKey(e)) stats.put(e, 1);
			else stats.put(e, stats.get(e) + 1);
		}
		return stats;
	}
	
	public static Map<String, Map<String, Map<String, Integer>>> traiterDossier(String url){
		
		Map<String, Map<String, Map<String, Integer>>> contacts = new HashMap<>();
		
		File dossier = new File(url);
		if (! (dossier.exists() && dossier.isDirectory()) ) return contacts;

		File[] fichiers = dossier.listFiles();
		
		for (File fichier: fichiers){
			String nomF = fichier.getName();
			if ((! nomF.endsWith(".html")) || nomF.startsWith(".")) continue;
			String titre = nomF.substring(0, nomF.length()-5);
			Map<String, List<String>> resultat = traiterFichier(fichier.getPath());
			
			Map<String, Map<String, Integer>> contact = new HashMap<>();
			contact.put("mails", traiterStats(resultat.get("mails")));
			contact.put("sites", traiterStats(resultat.get("sites")));
			contact.put("tels", traiterStats(resultat.get("tels")));
			
			contacts.put(titre, contact);
		}
		return contacts;
	}
	
	public static Map<String, Map<String, Map<String, Integer>>> traiterReference(String url){
		
		Map<String, Map<String, Map<String, Integer>>> contacts = new HashMap<>();
		
		try {
			BufferedReader f = new BufferedReader(new FileReader(url));
			String l;
			while((l = f.readLine()) != null) {
				String[] info = l.split("	");
				if (info.length < 4) continue;
				
				Map<String, Map<String, Integer>> contact;
				if (! contacts.containsKey(info[0])) {
					contact = new HashMap<String, Map<String, Integer>>();
					contacts.put(info[0], contact);
					contact.put("mails", new HashMap<String, Integer>());
					contact.put("sites", new HashMap<String, Integer>());
					contact.put("tels", new HashMap<String, Integer>());
				} else {
					contact = contacts.get(info[0]);
				}
				contact.get(info[1]).put(info[2], Integer.valueOf(info[3]));
			}
			f.close();
		} catch(IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		return contacts;
	}
	
	public static class Comparaison {
		public Map<String, Map<String, Map<String, String>>> contacts;
		public double R, P, F1;
	}
	
	public static Comparaison comparer(
			Map<String, Map<String, Map<String, Integer>>> sysContacts, 
			Map<String, Map<String, Map<String, Integer>>> refContacts){
		
		int INT = 0, // taille ref intersection sys
			SYS = 0, // taille sys
			REF = 0; // taille ref
		Map<String, Map<String, Map<String, String>>> resultat = new HashMap<>();
		
		for (String fichier: refContacts.keySet()) {
			Map<String, Map<String, String>> contact = new HashMap<>();
			resultat.put(fichier, contact);
			Map<String, Map<String, Integer>> refTypes = refContacts.get(fichier);
			Map<String, Map<String, Integer>> sysTypes = null;
			if (sysContacts.containsKey(fichier)) sysTypes = sysContacts.get(fichier);
			
			
	        for (String type: refTypes.keySet()){
	        	Map<String, String> resElements = new HashMap<>();
	        	contact.put(type, resElements);
	        	
	            for (String element: refTypes.get(type).keySet()){
	            	int refNbr = refTypes.get(type).get(element);
	                int sysNbr = 0;
	                if ((sysTypes != null) && (sysTypes.get(type).containsKey(element))){
	                    sysNbr = sysTypes.get(type).get(element);
	                }
	                resElements.put(element, "sys(" + sysNbr + "), ref(" + refNbr + ")");
	                SYS += sysNbr;
	                REF += refNbr;
	                INT += (sysNbr < refNbr)? sysNbr : refNbr;
	            }
	            if (sysTypes != null){
	                for (String element: sysTypes.get(type).keySet()){
	                    if (! resElements.containsKey(element)){
	                        int sysNbr = sysTypes.get(type).get(element);
	                        resElements.put(element, "sys(" + sysNbr + "), ref(0)");
	                        SYS += sysTypes.get(type).get(element);
	                    }
	                }
	            }
	        }
		}
		
		Comparaison comp = new Comparaison();
		comp.contacts = resultat;
		comp.R = ((double)INT) / REF;
		comp.P = ((double)INT) / SYS;
	    comp.F1 = 2 * comp.P * comp.R / (comp.P + comp.R);
	    return comp;
	}
	
	public static <T> void afficher(Map<String, Map<String, Map<String, T>>> contacts){
		for (String fichier: contacts.keySet()) {
	        System.out.println("========== " + fichier + " ==========");
	        Map<String, Map<String, T>> stats = contacts.get(fichier);
	        for (String type: stats.keySet()) {
	        	System.out.println("------> " + type);
	        	Map<String, T> statsType = stats.get(type);
	            for (String element: statsType.keySet()) {
	            	System.out.println("         " + element + " : " + statsType.get(element));
	            }
	        }
	    }
	}
	
	
	public static void main(String[] args) {
		//Map<String, List<String>> res = traiterFichier("/home/kariminf/CONTACT/mesrs.html");
		//System.out.print(res);
		String url = "./";
		if (args.length > 0) {
			url = args[0];
			if(! url.endsWith("/")) url += "/";
		}
		Map<String, Map<String, Map<String, Integer>>> sysContacts = traiterDossier(url);
		Map<String, Map<String, Map<String, Integer>>> refContacts = traiterReference(url + "ref.txt");
		Comparaison comp = comparer(sysContacts, refContacts);
		afficher(comp.contacts);
		System.out.println("---------------------------------------------------");
		System.out.println("R =" + comp.R + ", P =" + comp.P + ", F1 =" + comp.F1);
	}
}

