import java.io.File;
import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
/*
 * ServeurWeb.java
 * par Charles Hunter-Roy et Francis Clément, 2014
 * petit serveur web qui attend des connections, on peut le fermer à tout moment grâce à la classe Terminateur
 */
public class ServeurWeb {
	private int port;
	static final int DELAI = 500;
	static final int PORT_MIN = 0;
	static final int PORT_MAX = 65537;
	static int nbClients = 0;
	private boolean pasFini = true;
	private String filePath = "c:\\www\\";
	ServerSocket serveur = null;
	Thread threadTerminateur;

	public static void main(String args[]) {
		try {
			int port = 80;	
			String filePath = "c:\\www\\";
			switch( args.length )
		      {
				case 0:
					try
		            {		               
		            	ServeurWeb serveur = new ServeurWeb(port);
						serveur.lancerServeur();
		            }
		            catch( NumberFormatException nfe )
		            {
						System.err.println( "Le numero de port doit etre un nombre entier" );
		            }
					break;
		         case 1:
		            try
		            {
		               port = Integer.parseInt( args[ 0 ] );
		               
		               if( ( port >= PORT_MIN ) && ( port <= PORT_MAX ) )
		               {
		            	   ServeurWeb serveur = new ServeurWeb(port);
		            	   serveur.lancerServeur();
		               }
		               else
		               {
		                  System.err.println( "Le numero de port est hors intervale" );
		               }
		            }
		            catch( NumberFormatException nfe )
		            {
		               System.err.println( "Le numero de port doit etre un nombre entier" );
		            }
		            break;
		         case 2:
		        	 try
			            {
			               port = Integer.parseInt( args[ 0 ] );
			               filePath = args[1].toString();
			               
			               if(( port >= PORT_MIN ) && ( port <= PORT_MAX )  && new File(filePath).exists())
			               {
			            	   ServeurWeb serveur = new ServeurWeb(port, filePath);
			            	   serveur.lancerServeur();
			               }
			               else
			               {
			                  System.err.println( "Le numero de port est hors intervale" );
			               }
			            }
			            catch( NumberFormatException nfe )
			            {
			               System.err.println( "Le numero de port doit etre un nombre entier" );
			            }
		        	 break;
		         default:
		            System.err.println( "Il y a trop de parametres" );
		            break;
		      }

		} catch (Exception ex) {
			System.err.print(ex);
			System.exit(1);
		}
	}

	ServeurWeb(int nb) {
		this.port = nb;
		Terminateur test = new Terminateur();
		threadTerminateur = new Thread(test);
		threadTerminateur.start();
	}
	ServeurWeb(int nb, String path) {
		this.port = nb;
		this.filePath = path;
		Terminateur test = new Terminateur();
		threadTerminateur = new Thread(test);
		threadTerminateur.start();
	}

	public int getPort() {
		return this.port;
	}
	public String getFilePath() {
		return this.filePath;
	}
	public void bouclerServeur() {
		Thread threadClient;
		while (pasFini) {
			Socket socket;
			try {
				serveur.setSoTimeout(DELAI);
				socket = serveur.accept();				
				System.out.println("Ouverture d'une connexion");

				Client client = new Client(socket, getFilePath());

				threadClient = new Thread(client);
				threadClient.start();
				
				nbClients++;
				System.out.println("nb Clients: " + nbClients);
				
			} catch (SocketTimeoutException e) {
				pasFini = threadTerminateur.isAlive();
			} catch (SocketException e){
				System.out.println("Connection Interrompue");
			} catch (Exception e) {
				System.out.println(e);
			}
		}
	}

	public void lancerServeur() {
		try {
			serveur = new ServerSocket(port);
			System.out.println("Serveur en ligne (port=" + getPort() + "," + "racine=" + new File(filePath).getPath() + ")");
			bouclerServeur();

		} catch (SocketTimeoutException ex) {
			System.err.println("Délai expiré!");
		} catch (BindException ex) {
			System.err.println("Port(" + getPort() +  ") déjà utilisé!");
		} catch(SocketException e){
				System.out.println("Connection Interrompue");
		} catch (IOException ioe) {
			System.err.println("Erreur de traitement!");
		} catch (Exception ex) {
			System.err.println(ex);
		} finally {
			if (serveur != null) {
				System.out.println("Fermeture du serveur");
				try {
					serveur.close();
				} catch(SocketException e){
					System.out.println("Connection Interrompue");
				} catch (Exception ex) {
					System.err.println(ex);
				}
			}
			System.exit(1);
		}
	}
}
