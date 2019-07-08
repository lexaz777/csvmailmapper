package utils;

import javax.mail.*;
import javax.mail.internet.MimeUtility;
import javax.mail.search.FlagTerm;
import java.io.*;
import java.util.*;

public class MailReceiver {

    public List<byte[]> getMessages(String protocol, String host, String port,
                                    String username, String password, String requiredSenderAddress) {
        List<byte[]> attachments = new ArrayList<>();
        Properties serverProperties = getServerProperties(protocol, host, port);
        Session mailSession = Session.getInstance(serverProperties);
        try {
            Store store = mailSession.getStore();
            store.connect(username, password);

            Folder folderInbox = store.getFolder("INBOX");
            folderInbox.open(Folder.READ_WRITE);

            Flags seen = new Flags(Flags.Flag.SEEN);
            FlagTerm unseenFlagTerm = new FlagTerm(seen, false);
            Message[] messages = folderInbox.search(unseenFlagTerm);
            System.out.println("Найдено непрочитанных сообщений: " + messages.length);
            for (int i = 0; i < messages.length; i++) {
                Message msg = messages[i];
                Address[] fromAddress = msg.getFrom();
                String from = fromAddress[0].toString();

                if (!from.contains(requiredSenderAddress)) continue;

                msg.setFlag(Flags.Flag.SEEN, true);

                if (msg.isMimeType("multipart/mixed")) {
                    System.out.println("Найдено необработанное сообщение с вложениями.");
                    Multipart multipart = (Multipart) msg.getContent();
                    for (int j = 0; j < multipart.getCount(); j++) {
                        BodyPart part = multipart.getBodyPart(j);
                        if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                            String fileName = MimeUtility.decodeText(part.getFileName());
                            if (fileName.contains(".csv")) {
                                System.out.println("Найдено письмо с вложением csv.");
                                System.out.println("Начинаем загрузку вложения.");
                                attachments.add(getByteArray(part));
                            }
                        }
                    }
                }
            }

            store.close();

        } catch (NoSuchProviderException e) {
            System.out.println("No such provider.");
            e.printStackTrace();
        } catch (MessagingException e) {
            System.out.println("Connection to store error.");
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Error to process multipart content");
            e.printStackTrace();
        }

        return attachments;
    }

    private byte[] getByteArray(BodyPart part) throws IOException, MessagingException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        InputStream input = part.getInputStream();
        byte[] buffer = new byte[4096];
        int byteRead;
        while ((byteRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, byteRead);
        }
        return output.toByteArray();
    }

    private Properties getServerProperties(String protocol, String host,
                                           String port) {
        Properties properties = new Properties();

        properties.put("mail.store.protocol", protocol);
        properties.put("mail.imap.fetchsize", "2000000");
        // server setting
        properties.put(String.format("mail.%s.host", protocol), host);
        properties.put(String.format("mail.%s.port", protocol), port);

        // SSL setting
        properties.setProperty(
                String.format("mail.%s.socketFactory.class", protocol),
                "javax.net.ssl.SSLSocketFactory");
        properties.setProperty(
                String.format("mail.%s.socketFactory.fallback", protocol),
                "false");
        properties.setProperty(
                String.format("mail.%s.socketFactory.port", protocol), port);

        return properties;
    }
}
