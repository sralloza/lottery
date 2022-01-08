package es.sralloza.lottery.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.springframework.stereotype.Service;

import es.sralloza.lottery.exceptions.ServerError;

@Service
public class HTTPService {
    public String get(String url) throws ServerError {
        try {
            URL realURL = new URL(url);
            HttpURLConnection con = (HttpURLConnection) realURL.openConnection();

            con.setRequestMethod("GET");

            int status = con.getResponseCode();
            if (400 <= status && status <= 599) {
                throw new ServerError(String.format("Received %d from official lottery API", status));
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            con.disconnect();
            return content.toString();
        } catch (IOException e) {
            e.printStackTrace();
            throw new ServerError(e.toString());
        }
    }
}
