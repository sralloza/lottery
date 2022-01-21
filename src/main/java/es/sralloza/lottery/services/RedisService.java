package es.sralloza.lottery.services;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import es.sralloza.lottery.exceptions.ParseError;
import es.sralloza.lottery.models.Lottery;
import es.sralloza.lottery.models.LotteryTicket;
import redis.clients.jedis.JedisPooled;

@Repository("redis")
public class RedisService {
    private JedisPooled pool;
    private String redisHost;
    private int redisPort;

    public RedisService() {
        redisHost = System.getenv("REDIS_HOST");
        redisHost = redisHost == null ? "localhost" : redisHost;

        String redisPortStr = System.getenv("REDIS_PORT");
        redisPort = redisPortStr == null ? 6379 : Integer.parseInt(redisPortStr);

        System.out.printf("Connecting to redis %s:%d\n", redisHost, redisPort);
        pool = new JedisPooled(redisHost, redisPort);
    }

    // Updates
    public LocalDateTime getLastUpdate() {
        String result = pool.get("LAST_UPDATE");
        if (result == null)
            return null;

        LocalDateTime date = LocalDateTime.parse(result);
        return date;
    }

    public void setLastUpdateNow() {
        LocalDateTime date = LocalDateTime.now();
        pool.set("LAST_UPDATE", date.toString());
    }

    // Lottery
    private String stripLotteryDate(String date) {
        return date.replace("LOTTERY_", "");
    }

    public void saveMultipleLotteries(List<Lottery> lotteries) {
        List<String> msetArgs = new ArrayList<>();
        for (Lottery lottery : lotteries) {
            msetArgs.add("LOTTERY_" + lottery.getDateString());
            msetArgs.add(lottery.getId().toString());
        }
        String[] args = msetArgs.toArray(new String[0]);
        pool.mset(args);
    }

    public List<Lottery> getLotteriesIDs() {
        Set<String> keys = pool.keys("LOTTERY_*");
        List<String> orderedKeys = new ArrayList<>(keys);
        String[] args = orderedKeys.toArray(new String[0]);
        List<String> values = pool.mget(args);

        List<Lottery> result = new ArrayList<>();
        SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd");

        for (Integer i = 0; i < orderedKeys.size(); i++) {
            String dateString = stripLotteryDate(orderedKeys.get(i));
            Date date;
            try {
                date = parser.parse(dateString);
            } catch (ParseException e) {
                e.printStackTrace();
                throw new ParseError(String.format("Can't parse database date %s", dateString));
            }
            Lottery lottery = new Lottery(Integer.parseInt(values.get(i)), date);
            result.add(lottery);
        }
        return result;
    }

    public void saveLottery(Lottery lottery) {
        pool.set("LOTTERY_" + lottery.getDateString(), lottery.getId().toString());
    }

    public Lottery getLotteryByDate(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String result = pool.get("LOTTERY_" + formatter.format(date));
        if (result == null)
            return null;
        Integer id = Integer.parseInt(result);
        return new Lottery(id, date);
    }

    // Lottery tickets
    public void saveMultipleLotteryTickets(List<LotteryTicket> numbers, Lottery lottery) {
        List<String> msetArgs = new ArrayList<>();
        for (LotteryTicket ticket : numbers) {
            msetArgs.add(String.format("TICKET_%s_%s", lottery.getDateString(), ticket.number));
            msetArgs.add(ticket.prize.toString());
        }
        String[] args = msetArgs.toArray(new String[0]);
        pool.mset(args);
    }

    public void saveLotteryTicket(LotteryTicket ticket, Lottery lottery) {
        pool.set(String.format("TICKET_%s_%s", lottery.getDateString(), ticket.number), ticket.prize.toString());
    }

    public LotteryTicket getLotteryTicketByNumber(Integer number) {
        String result = pool.get("TICKET_" + number.toString());
        if (result == null)
            return null;
        Integer prize = Integer.parseInt(result);
        return new LotteryTicket(number, prize);
    }
}
