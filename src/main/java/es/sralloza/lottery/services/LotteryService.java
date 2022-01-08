package es.sralloza.lottery.services;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import es.sralloza.lottery.exceptions.ParseError;
import es.sralloza.lottery.models.Lottery;

@Service
public class LotteryService {
    private final RedisService redis;
    private HTTPService httpService;

    @Autowired
    public LotteryService(@Qualifier("redis") RedisService redis, HTTPService httpService) {
        this.redis = redis;
        this.httpService = httpService;
    }

    public void loadLotteries() {
        String content = httpService.get("https://www.loteriasyapuestas.es/es/loteria-nacional");
        Document doc = Jsoup.parse(content);
        Element select = doc.getElementById("qa_subhome-comprobador-fecha-LNAC");
        Elements options = select.getElementsByTag("option");

        List<Lottery> result = new ArrayList<>();
        SimpleDateFormat parser = new SimpleDateFormat("dd-MM-yyyy");

        for (Element el : options) {
            String dateString = el.attr("data-date").replace("/", "-");
            Integer lotteryID = Integer.parseInt(el.attr("value"));
            Date date;
            try {
                date = parser.parse(dateString);
            } catch (ParseException e) {
                e.printStackTrace();
                throw new ParseError(String.format("Can't parse HTTP date %s", dateString));
            }
            result.add(new Lottery(lotteryID, date));
        }
        redis.saveMultipleLotteries(result);
    }

    public List<Lottery> getLotteries() {
        return redis.getLotteriesIDs();
    }

    public Lottery getLotteryByID(Integer id) {
        List<Lottery> lotteries = getLotteries();
        for (Lottery lottery : lotteries) {
            if (lottery.getId() == id)
                return lottery;
        }
        return null;
    }

    public Lottery getLotterybyDate(Date date) {
        return redis.getLotteryByDate(date);
    }
}
