import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

/*
 * Client.java
 * par Charles Hunter-Roy et Francis Clément, 2014
 * petit client web qui se connecte à un serveur pour y lire des fichiers
 * 
 */
public class ClientWeb {

	private Socket socket;
	private String savePath = "C:\\Users\\Charles\\Desktop\\Charles\\Test Programmes\\Communication\\Save\\";

	private String prompt = "Fichier a recuperer: ";

	// constructeur paramétrique
	public ClientWeb(Socket socket) {
		this.socket = socket;
	}

	public ClientWeb(String host, int port) {
		try {
			this.socket = new Socket(host, port);
		} catch (Exception e) {

		}
	}

	public static void main(String args[]) {
		final String host = "localhost";
		final int portNumber = 85;
		final int DELAI = 500;
		try {
			ClientWeb client = new ClientWeb(host, portNumber);

			BufferedReader br = new BufferedReader(new InputStreamReader(
					client.socket.getInputStream()));

			PrintWriter sOut = new PrintWriter(client.socket.getOutputStream(),
					true);

			BufferedReader userInputBR = new BufferedReader(
					new InputStreamReader(System.in));

			PrintWriter out = new PrintWriter(System.out, true);

			boolean fini = false;
			while (!fini) {
				String texte = br.readLine();
				if (texte == null || texte.endsWith("fichier(s) disponible(s)")) {
					if (texte.endsWith("fichier(s) disponible(s)")) {
						out.println(texte);
					}
					fini = true;
				} else {
					out.println(texte);
				}
			}
			out.print("Fichier a recuperer: ");
			out.flush();
			String userInput = userInputBR.readLine();
			sOut.println("GET " + userInput);
			String code = br.readLine();

			if (code.equals("=>200 Ok")) {
				out.println("200 Ok");
				out.flush();
				BufferedInputStream input = null;
				BufferedOutputStream output = null;

				input = new BufferedInputStream(client.socket.getInputStream());

				output = new BufferedOutputStream(new FileOutputStream(userInput));

				byte[] byteBuffer = new byte[1024];

				while (input.read(byteBuffer) != -1) {
 					output.write(byteBuffer);
					output.flush();
				}
				
				input.close();
				output.close();
			}

		} catch (SocketTimeoutException e) {
			System.err.println("Connection rompue!");
		} catch (SocketException e) {
			System.err.println("Connection rompue!");
		} catch (Exception e) {
			System.err.println("erreur Inattendue: "+ e);
			e.printStackTrace();
		}
	}
}
