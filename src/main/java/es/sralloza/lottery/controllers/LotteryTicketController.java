package es.sralloza.lottery.controllers;

import java.util.Date;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import es.sralloza.lottery.models.Lottery;
import es.sralloza.lottery.models.LotteryTicket;
import es.sralloza.lottery.services.LotteryService;
import es.sralloza.lottery.services.LotteryTicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController()
@RequestMapping("/tickets")
@Tag(name = "Ticket", description = "Check Prizes of lottery tickets")
public class LotteryTicketController {
    private final LotteryTicketService lotteryTicketService;
    private final LotteryService lotteryService;

    @Autowired
    public LotteryTicketController(LotteryTicketService lotteryTicketService, LotteryService lotteryService) {
        this.lotteryTicketService = lotteryTicketService;
        this.lotteryService = lotteryService;
    }

    @Operation(summary = "Load Lottery Tickets", description = "Load prized lottery tickets for a specific lottery.")
    @GetMapping(value = "/load", produces = "application/json")
    public String loadLotteryTickets(@RequestParam(value = "id", required = false) Integer lotteryID,
            @RequestParam(value = "date", required=false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date lotteryDate) {
        ResponseStatusException exc = new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Must pass id or date parameters but not both");
        if (lotteryID == null && lotteryDate == null)
            throw exc;
        if (lotteryID != null && lotteryDate != null)
            throw exc;

        Lottery lottery;
        if (lotteryID != null)
            lottery = lotteryService.getLotteryByID(lotteryID);
        else
            lottery = lotteryService.getLotterybyDate(lotteryDate);

        lotteryTicketService.loadLotteryTickets(lottery);
        return "ok";
    }

    @GetMapping(value = "/load/{lotteryDate}", produces = "application/json")
    public String loadLotteryTicketsByLotteryDate(
            @PathVariable("lotteryDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date lotteryDate) {
        Lottery lottery = lotteryService.getLotterybyDate(lotteryDate);
        lotteryTicketService.loadLotteryTickets(lottery);
        return "ok";
    }

    @Operation(summary = "Get lottery ticket prize", description = "Returns the lottery ticket prize. The prize is related to the entire series (10 tickets).")
    @ApiResponse(responseCode = "200", description = "Successful Response", content = @Content(schema = @Schema(implementation = Integer.class)))
    @GetMapping(path = "/{number}", produces = "application/json")
    public LotteryTicket getLotteryTicketPrize(
            @Parameter(description = "Lottery ticket number") @PathVariable("number") @Valid @Min(0) @Max(99999) Integer number) {
        System.out.println(number);
        return lotteryTicketService.getLotteryTicketByNumber(number);
    }
}
