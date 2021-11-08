package kz.codesmith.epay.loan.api.requirement.primitive;

import com.creditinfo.ws.score.ScoreData;
import kz.codesmith.epay.loan.api.annotation.SpringRequirement;
import kz.codesmith.epay.loan.api.model.exception.ScoringUnreachableException;
import kz.codesmith.epay.loan.api.model.scoring.RejectionReason;
import kz.codesmith.epay.loan.api.model.scoring.ScoringVars;
import kz.codesmith.epay.loan.api.requirement.Requirement;
import kz.codesmith.epay.loan.api.requirement.RequirementResult;
import kz.codesmith.epay.loan.api.requirement.ScoringContext;
import kz.codesmith.epay.loan.api.service.IPkbScoreService;
import kz.com.fcb.fico.Result;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@SpringRequirement
@RequiredArgsConstructor
@Slf4j
public class ScoreAndBadRateRequirement implements Requirement<ScoringContext> {

  private final IPkbScoreService pkbScoreService;
  private int minScoreBall;
  private double maxPdBadRate;

  @Override
  @SneakyThrows
  public RequirementResult check(ScoringContext context) {
    var requestData = context.getRequestData();
    minScoreBall = context.getVariablesHolder().getValue(ScoringVars.MIN_SCORE_BALL, Integer.class);
    maxPdBadRate = context.getVariablesHolder()
        .getValue(ScoringVars.MAX_PD_BAD_RATE, Integer.class);
    var iin = requestData.getIin();
    var useFicoScoring = context.getVariablesHolder()
        .getValue(ScoringVars.FICO_SCORING, Boolean.class);
    var useBehvScoring = context.getVariablesHolder()
        .getValue(ScoringVars.BEHAVIORAL_SCORING, Boolean.class);
    var isWhitelist = context.getRequestData().isWhiteList();
    log.info("Scoring context: {}", context.toString());

    if (isWhitelist) {
      log.info("IIN in whitelist {}, no scoring", iin);
      //TODO move to config
      context.getScoringInfo().setScore(2);
      context.getScoringInfo().setBadRate(2.0);
      return RequirementResult.success();
    }

    if (useFicoScoring) {
      RequirementResult ficoResult = callFicoScoring(context, iin);

      if (ficoResult == null) {
        RequirementResult behvResult = callBehaviorScoring(context, iin);

        if (behvResult == null) {
          log.info("FICO and Behavior Scores are both null for {}", iin);
          return RequirementResult.failure(RejectionReason.BAD_SCORE_OR_RATE);
        } else if (behvResult.isSuccessful()) {
          return RequirementResult.success();
        } else {
          return RequirementResult.failure(RejectionReason.BAD_SCORE_OR_RATE);
        }

      } else if (ficoResult.isSuccessful()) {
        return RequirementResult.success();

      } else {
        return RequirementResult.failure(RejectionReason.BAD_SCORE_OR_RATE);
      }

    } else if (useBehvScoring) {
      RequirementResult behvScoring = callBehaviorScoring(context, iin);

      if (behvScoring == null) {
        RequirementResult ficoResult = callFicoScoring(context, iin);

        if (ficoResult == null) {
          log.info("FICO and Behavior Scores are both null for {}", iin);
          return RequirementResult.failure(RejectionReason.BAD_SCORE_OR_RATE);
        } else if (ficoResult.isSuccessful()) {
          return RequirementResult.success();
        } else {
          return RequirementResult.failure(RejectionReason.BAD_SCORE_OR_RATE);
        }

      } else if (behvScoring.isSuccessful()) {
        return RequirementResult.success();

      } else {
        return RequirementResult.failure(RejectionReason.BAD_SCORE_OR_RATE);
      }

    }

    throw new IllegalStateException();
  }

  private RequirementResult callFicoScoring(ScoringContext context, String iin) {
    Result result = pkbScoreService.getFicoScore(iin);
    //it seems result and score never returned as null
    if (result.getBadRate() != null) {
      context.getScoringInfo().setScore(result.getScore());
      context.getScoringInfo().setBadRate(parseBadRate(result.getBadRate()));

      if (hasGoodScoreAndBadRate(result.getScore(), result.getBadRate())) {
        log.info("PKB FicoScoring for {} returned {}", iin, "SUCCESS");
        return RequirementResult.success();
      }
      log.info("PKB FicoScoring for {} returned {}", iin, "FAIL");
      return RequirementResult.failure(RejectionReason.BAD_SCORE_OR_RATE);
    }
    return null;
  }

  private RequirementResult callBehaviorScoring(ScoringContext context, String iin) {
    ScoreData result = pkbScoreService.getBehaviorScore(iin, true);
    //it seems result and score never returned as null
    if (result.getBadRate() != null) {
      context.getScoringInfo().setScore((int) result.getScore());
      context.getScoringInfo().setBadRate(parseBadRate(result.getBadRate()));

      if (hasGoodScoreAndBadRate((int) result.getScore(), result.getBadRate())) {
        log.info("PKB BehaviorScoring for {} returned {}", iin, "SUCCESS");
        return RequirementResult.success();
      }
      log.info("PKB BehaviorScoring for {} returned {}", iin, "FAIL");
      return RequirementResult.failure(RejectionReason.BAD_SCORE_OR_RATE);
    }
    return null;
  }


  private boolean hasGoodScoreAndBadRate(int score, String badRate) {
    log.info(
        "Scoring [minScoreBall={} userScore={} maxBadRate={} userBadRate={}]",
        minScoreBall,
        score,
        maxPdBadRate,
        badRate
    );
    //Behavior Scoring [minScoreBall=250 userScore=500 maxBadRate=60.0 userBadRate=52,13%]
    return score >= minScoreBall && parseBadRate(badRate) <= maxPdBadRate;
  }

  private Double parseBadRate(String badRate) {
    //removing last % char and parsing to double to compare
    String substr = badRate.substring(0, badRate.length() - 1);
    return Double.parseDouble(substr.replace(",", "."));
  }

}
