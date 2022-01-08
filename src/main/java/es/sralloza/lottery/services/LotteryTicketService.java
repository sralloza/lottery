package es.sralloza.lottery.services;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.lang.reflect.Type;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import es.sralloza.lottery.exceptions.ServerError;
import es.sralloza.lottery.models.Lottery;
import es.sralloza.lottery.models.PrizedLotteryTicket;

@Service
public class LotteryTicketService {
    private final RedisService redis;
    private HTTPService httpService;

    @Autowired
    public LotteryTicketService(@Qualifier("redis") RedisService redis, HTTPService httpService) {
        this.redis = redis;
        this.httpService = httpService;
    }

    public Integer getMoneyWon(Integer number, Integer moneyBet) {
        PrizedLotteryTicket ticket = getLotteryTicketByNumber(number);
        if (ticket == null) return 0;
        return ticket.prize * moneyBet / 2000;
    }

    public PrizedLotteryTicket getLotteryTicketByNumber(Integer number) {
        PrizedLotteryTicket ticket = redis.getLotteryTicketByNumber(number);
        if (ticket == null)
            return null;
        return ticket;

    }

    public void loadLotteryTickets(Lottery lottery) throws ServerError {
        LocalDateTime lastUpdate = redis.getLastUpdate();
        if (lastUpdate != null) {
            Duration diff = Duration.between(lastUpdate, LocalDateTime.now());
            if (diff.toMinutes() <= 5) {
                return;
            }
        }
        String url = String.format("https://www.loteriasyapuestas.es/servicios/premioDecimoWeb?idsorteo=%d",
                lottery.getId());
        String content = httpService.get(url);
        JSONObject responseJson = new JSONObject(content.toString());
        String jsonTickets = responseJson.getJSONArray("compruebe").toString().replace("decimo", "number");

        Gson gson = new Gson();
        Type resultType = new TypeToken<List<PrizedLotteryTicket>>() {
        }.getType();

        List<PrizedLotteryTicket> lotteryTickets = gson.fromJson(jsonTickets, resultType);
        redis.saveMultipleLotteryTickets(lotteryTickets, lottery);
        redis.setLastUpdateNow();
        return;

    }
}
