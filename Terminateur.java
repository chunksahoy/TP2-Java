/*
 * Terminateur.java
 * par Charles Hunter-Roy, 2014
 * but: cr�ation d'un thread qui affiche des points � la console tant que l'utilisateur n'a pas entr� une touche sp�cifi�e (mon premier Thread wooooo!)
 *
 **/

import java.util.Scanner;

class Terminateur implements Runnable {
	boolean estValide = true;
	private final String TERMIN = "Q";
	public void run() {
		Scanner scan = new Scanner(System.in);
		String in = "";
		while (estValide) {
			in = scan.nextLine();
			if (in.trim().equalsIgnoreCase(TERMIN)) {
				estValide = false;
			}
		}
	}
}
