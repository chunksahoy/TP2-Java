import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.*;
import java.util.ArrayList;


/*
 * Client.java
 * par Charles Hunter-Roy et Francis Clément, 2014
 * petit client web qui se connecte à un serveur pour y lire des fichiers
 * 
 */
public class Client implements Runnable {

	private final int DELAI = 500;
	private Socket socket;
	private String filePath;
	private ArrayList<String> commandes = new ArrayList<String>();
	private File fichier;
	private File[] listeFichiers;
	private int nbFichiers = 0;
	private String prompt = "=>";

	// constructeur paramétrique
	public Client(Socket socket) {
		this.socket = socket;
		initialiserCommandes();
	}

	public Client(Socket socket, String path) {
		this.socket = socket;
		this.filePath = path;
		initialiserCommandes();
		initialiserListeFichiers();
	}

	private void initialiserListeFichiers() {
		fichier = new File(filePath);
		listeFichiers = fichier.listFiles();
	}

	// initialisation des commandes valides
	public void initialiserCommandes() {
		commandes.add("GET");
	}

	// vérifie si la ligne passé en paramètre est dans la liste de commandes
	// valides
	private boolean verifierCommande(String ligne) {
		boolean valide = false;
		for (int i = 0; i < commandes.size() && !valide; ++i) {
			if (ligne.toUpperCase().startsWith(commandes.get(i))
					&& ligne != null) {
				valide = true;
			}
		}
		return valide;
	}

	private boolean entreeValide(String commande) {
		boolean valide = true;
		if (commande.split("\\s+").length <= 0
				|| commande.split("\\s+").length > 2) {
			valide = false;
		}
		return valide;
	}

	private void listerContenu(PrintWriter writer, File[] liste) {
		for (int i = 0; i < liste.length; i++) {
			if (liste[i].isFile()) {
				nbFichiers++;
				writer.printf("%-30s %-10s %tD %n",	liste[i].getName(), liste[i].length(), liste[i].lastModified());
			} else if (liste[i].isDirectory()) {
				nbFichiers++;
				writer.printf("%-41s %tD %n", "[]" + liste[i].getName(), liste[i].lastModified());
			}
		}
	}
	private void ecrireLigne(PrintWriter writer, String msg) {
		writer.println(msg);
	}
	private void ecrire(PrintWriter writer, String msg) {
		writer.print(msg);
	}
	private void ecrireFormater(PrintWriter writer, String format, String[] msg) {
		writer.printf(format, msg.toString());
	}
	private String getCommandes(String ligne) {
		boolean trouve = false;
		String commande = "";
		for (int i = 0; i < commandes.size() && !trouve; ++i) {
			if (ligne.toUpperCase().startsWith(commandes.get(i))
					&& ligne != null) {
				trouve = true;
				commande = commandes.get(i);
			}
		}
		return commande;
	}

	public void run() {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));			
			
			PrintWriter writer = new PrintWriter(new OutputStreamWriter(
					socket.getOutputStream()), true);
			
			ecrireLigne(new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true),"Serveur Web v0.2 par Charles Hunter-Roy et Francis Clement\r" );
			ecrireLigne(new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true),"Contenu du dossier " + filePath );
			
			listerContenu((new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true)), listeFichiers);
			
			ecrireLigne(new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true), nbFichiers + " fichier(s) disponible(s)");
			
			boolean pasFini = true;

			while (pasFini) {
				writer.print(prompt);
				writer.flush();
				String ligne = reader.readLine();
				String commande = "200 Ok";
				
				if (verifierCommande(ligne.trim())) {					
					String fichier = ligne.trim().substring(getCommandes(ligne.trim()).length(), ligne.trim().length()).trim();
					File file = new File(filePath + "\\" + fichier);

					if(!entreeValide(ligne.trim()) && !file.exists()) {
						commande = "400 Mauvaise Requete";
						writer.println(commande);
						pasFini = false;
						Thread.sleep(DELAI);
					}
					
					String temp = "temp";
					
					if (file.exists()) {
						writer.println(commande);
						if (file.isDirectory()) {
							File[] liste = file.listFiles();
							listerContenu((new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true)), liste);

							writer.println(nbFichiers + " fichier(s) disponible(s)");
							
						} else if (file.isFile()) {

							FileInputStream fis = new FileInputStream(filePath + "\\" + fichier);
	                        OutputStream os = new BufferedOutputStream(socket.getOutputStream());

							byte[] byteBuffer = new byte[1024];
							
							while (fis.read(byteBuffer) != -1) {
								os.write(byteBuffer);
								os.flush();
							}
							fis.close();
							os.close();
						}
						Thread.sleep(DELAI);
					} else if (!file.exists() && pasFini) {
						commande = "404 Fichier Inexistant";
						writer.println(commande);
						pasFini = false;
						Thread.sleep(DELAI);
					}
				} else if (!verifierCommande(ligne.trim()) && pasFini) {
					commande = "501 Commande Non-Implementee";
					writer.println(commande);
					pasFini = false;
					Thread.sleep(DELAI);
				}
			}
			System.out.println("fermeture d'une connexion "
					+ ServeurWeb.nbClients);
			reader.close();
			writer.close();
			socket.close();
		} catch(EOFException e) {
			
		} catch(SocketException e){
			//System.out.println("Connection Interrompue");
		} catch (InterruptedException e) {
			System.err.println("Connection interrompue!");
		} catch (IOException e) {
			System.err.println(e);
		} catch (NullPointerException e) {
			System.err.println("Client interrompu");
		} catch (Exception e) {
			System.err.println(e);
		} finally {
			ServeurWeb.nbClients--;
			System.out.println("Nb clients: " + ServeurWeb.nbClients);
		}
	}

	public void start() {
		
	}
}
