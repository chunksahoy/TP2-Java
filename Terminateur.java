/*
 * Terminateur.java
 * par Charles Hunter-Roy, 2014
 * but: création d'un thread qui affiche des points à la console tant que l'utilisateur n'a pas entré une touche spécifiée (mon premier Thread wooooo!)
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
