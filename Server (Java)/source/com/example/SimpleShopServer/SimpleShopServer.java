package com.example.SimpleShopServer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;

import java.net.ServerSocket;
import java.net.Socket;


public class SimpleShopServer {	

	private final static int VERSION_MSG = 1;
	private final static int UPLOAD_MSG  = 2;
	private final static int ORDER_MSG 	 = 8;	
	private final static int IMAGE_MSG 	 = 9;
	 	
	private static int version = 0;
	private static int orderCounter = 0;

	public static void main(String[] args) {
		File file = new File("./products");
		// Смотрим существует ли директория products
		if (!file.exists()) {
			// Если не существует создаём новую директорию
			file.mkdir();
		}
		// Выбераем из всех файлов в директории products файл с наибольшей версией
		File[] arrFiles = file.listFiles();
		if (arrFiles.length != 0) {
			for (File f : arrFiles) {
				if (!f.isDirectory()) {
					int n = Integer.parseInt(f.getName().substring(0, f.getName().lastIndexOf('.')));
					if (version < n) {
						version = n;
					}
				}					
			}
			
			System.out.println("ProductList version: " + Integer.toString(version));
			// После этого запускаем сервер и ждем подключения клиента
			try {
				ServerSocket serverSock = new ServerSocket(4242);
				while (true) {			
					Socket clientSocket = serverSock.accept();
					// При подключении запускаем новый поток
					Thread thread = new Thread(new Runnable() {
						@Override
						public void run() {
							clientDialogue(clientSocket);
							try {
								clientSocket.close();
							} catch (Exception e) {
								e.printStackTrace();
							}		
						}
					});
					thread.start();
				}				
			} catch (Exception e) {
				e.printStackTrace();
			}	
		} else {
			System.out.println("File productList not found");
		}	
	}
	
	
	// Функция диалога клинта с сервером
	private static void clientDialogue(Socket clientSocket) {
		try {
			// Получаем запрос клиента
			InputStream inputStream = clientSocket.getInputStream();
			int message = inputStream.read();		
			OutputStream outputStream = clientSocket.getOutputStream();

			byte[] buffer = new byte[1024];
			int length;			
			switch (message) {
				case VERSION_MSG: // Клиент просит передать актуальную версию productList
					outputStream.write(version);
					break;
				case UPLOAD_MSG: // Клиент просит передать актуальный файл productList
					sendFile(outputStream, "./products/" + Integer.toString(version) + ".xml");				
					break;
				case ORDER_MSG: // Клиент передаёт заказ
					length = inputStream.read(buffer);
					orderCounter++;
					System.out.println("#" + Integer.toString(orderCounter));
					System.out.println(new String(buffer).substring(0,length));
					System.out.println("");
					break;
				case IMAGE_MSG: // Клиент запрашивает изображение товара
					length = inputStream.read(buffer);
					String imageName = new String(buffer).substring(0,length);
					sendFile(outputStream, "./images/" + imageName);		
					break;
			}
			
			inputStream.close();
			outputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

	private static void sendFile(OutputStream outputStream, String fileName) throws Exception {
		int length;	
		byte[] buffer = new byte[1024];
		File file = new File(fileName);
		FileInputStream fileInput = new FileInputStream(file);					
		while ((length = fileInput.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }
		fileInput.close();
	}
}