package es.sralloza.lottery.models;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class LotteryTicket {
    public final Integer number;
    public final Integer prize;
}
