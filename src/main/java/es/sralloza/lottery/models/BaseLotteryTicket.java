package es.sralloza.lottery.models;

public class BaseLotteryTicket {
    public Integer number;
    public Integer prize = 0;

    public BaseLotteryTicket(Integer number, Integer prize) {
        this.number = number;
        this.prize = prize;
    }
}
