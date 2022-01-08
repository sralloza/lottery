package es.sralloza.lottery.controllers;

import java.util.Date;
import java.util.List;

import javax.validation.constraints.Min;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import es.sralloza.lottery.exceptions.NotFoundException;
import es.sralloza.lottery.models.Lottery;
import es.sralloza.lottery.services.LotteryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController()
@RequestMapping("/lotteries")
@Tag(name = "Lotteries", description = "Lotteries operations. See "
        + "[national lottery](https://www.loteriasyapuestas.es/es/loteria-nacional)"
        + " for more info.")
public class LotteryController {
    private final LotteryService lotteryService;

    @Autowired
    public LotteryController(LotteryService lotteryService) {
        this.lotteryService = lotteryService;
    }

    @Operation(summary = "Load lotteries info", description = "Load lotteries info. This process is done automatically every 5 minutes.")
    @GetMapping(value = "/load", produces = "application/json")
    public String loadLotteries() {
        lotteryService.loadLotteries();
        return "ok";
    }

    @Operation(summary = "Get one lottery", description = "Get lottery using its ID or date.")
    @GetMapping(path = "", produces = "application/json")
    public Lottery getOneLottery(@RequestParam(value = "id", required = false) @Min(1) Integer id,
            @RequestParam(value = "date", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {

        ResponseStatusException exc = new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Must pass id or date parameters but not both");
        if (id == null && date == null)
            throw exc;
        if (id != null && date != null)
            throw exc;

        Lottery lottery;
        String explanation;
        if (id != null) {
            lottery = lotteryService.getLotteryByID(id);
            explanation = "Lottery with id=" + id + " could not be found";
        } else {
            lottery = lotteryService.getLotterybyDate(date);
            explanation = "Lottery with date=" + date + " could not be found";
        }
        if (lottery == null) {
            System.out.println(explanation);
            throw new NotFoundException(explanation);
        }
        return lottery;
    }

    @Operation(summary = "Get all loteries", description = "Get all loteries info.")
    @GetMapping(path = "/all", produces = "application/json")
    public List<Lottery> getLotteries() {
        return lotteryService.getLotteries();
    }
}
