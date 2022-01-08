package es.sralloza.lottery.schedule;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import es.sralloza.lottery.models.Lottery;
import es.sralloza.lottery.services.LotteryService;
import es.sralloza.lottery.services.LotteryTicketService;

@Component
public class LoadData {
	private static final Logger log = LoggerFactory.getLogger(LoadData.class);
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

	private LotteryTicketService lotteryTicketService;
	private LotteryService lotteryService;

	@Autowired
	public LoadData(LotteryTicketService lotteryTicketService, LotteryService lotteryService) {
		this.lotteryTicketService = lotteryTicketService;
		this.lotteryService = lotteryService;
	}

	@Scheduled(fixedRate = 5 * 60 * 1000)
	public void loadPrizes() {
		log.info("Getting lottery IDs at {}", dateFormat.format(new Date()));
		lotteryService.loadLotteries();
		List<Lottery> lotteryIDS = lotteryService.getLotteries();
		log.info("Loading lottery tickets at {}", dateFormat.format(new Date()));
		for (Lottery lottery : lotteryIDS) {
			log.info("Loading lottery {} at {}", lottery.getDateString(), dateFormat.format(new Date()));
			lotteryTicketService.loadLotteryTickets(lottery);
			log.info("{} tickets loaded at {}", lottery.getDateString(), dateFormat.format(new Date()));
		}
		log.info("Lottery tickets loaded at {}", dateFormat.format(new Date()));
	}
}
