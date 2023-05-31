package src;

import static src.exception.ErrorCode.*;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import src.exception.InvalidInputFormatException;
import src.hangman.GameLauncher;
import src.hangman.HangmanGame;
import src.hangman.HangmanRound;
import src.repository.GameRepository;
import src.repository.RoundRepository;
import src.util.InputHelper;

public class HangManApplication {
    private final static GameRepository gameRepository = GameRepository.getInstance();
    private final static RoundRepository roundRepository = RoundRepository.getInstance();

    public static void main(String[] args) {
        while (true){
            int inputs = InputHelper.singleIntegerInput(RootMenu.chooseDescription());
            RootMenu rootMenu=RootMenu.findByCode(inputs);
            rootMenu.run();

            if(rootMenu == RootMenu.END){
                break;
            }
        }
    }

    private enum RootMenu{
        PLAY_GAME("게임하기",1, HangManApplication::playGame),
        SHOW_GAME_RESULT("게임 결과 보기",2,HangManApplication::showGameResult),
        SHOW_ROUND_RESULT("라운드 결과 보기",3,HangManApplication::showRoundResult),
        END("종료",4,() -> {
            InputHelper.printInfo("프로그램을 종료합니다.");
            return null;
        });

        String description;
        Integer code;
        Supplier<Void> function;

        RootMenu(String description, Integer code, Supplier<Void> function) {
            this.description = description;
            this.code = code;
            this.function = function;
        }

        public Void run(){
            return function.get();
        }

        public static RootMenu findByCode(int code){
            return Arrays.stream(RootMenu.values())
                .filter(m -> m.getCode() == code)
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
        }
        public static String chooseDescription(){
            StringBuilder sb = new StringBuilder();
            sb.append("메뉴를 선택합니다. (");
            sb.append(
                Arrays.stream(RootMenu.values())
                    .map(RootMenu::toString)
                    .collect(Collectors.joining(", "))
                    .toString()
            );
            sb.append(")");

            return sb.toString();
        }

        public Integer getCode() {
            return code;
        }

        @Override
        public String toString() {
            return code + " : " + description  ;
        }
    }

    private static Void playGame() {
        int playCount;
        int life;

        while (true){
            try{
                Integer[] inputs = InputHelper.multiIntegerInput("게임 횟수와 목숨을 입력하세요.");

                if (inputs.length != 2) {
                    throw new InvalidInputFormatException(INVALID_INPUT_COUNT.getMessage());
                }

                playCount = inputs[0];
                life = inputs[1];
                break;
            }catch(InvalidInputFormatException e){
               InputHelper.printInfo(e.getMessage());
            }
        }

        while (playCount-->0){
            GameLauncher gameLauncher =new GameLauncher(life);
            gameLauncher.start();

            if(playCount>0){
                InputHelper.printInfo("다음 게임을 시작합니다.");
            }
        }

        return null;
    }
    private static Void showGameResult() {
        if(gameRepository.isEmpty()){
            InputHelper.printInfo(NO_GAME_HISTORY.getMessage());
            return null;
        }

        while (true){
            try{
                int input = InputHelper.singleIntegerInput("게임 id를 입력해주세요.");

                HangmanGame game =gameRepository.getByGameId(input);
                InputHelper.printInfo(game.resultString());
                break;
            }catch(NoSuchElementException e){
                InputHelper.printInfo(e.getMessage());
            }
        }
        return null;
    }

    private static Void showRoundResult() {
        if(roundRepository.isEmpty()){
            InputHelper.printInfo(NO_GAME_HISTORY.getMessage());
            return null;
        }

        while (true){
            try{
                int input = InputHelper.singleIntegerInput("라운드 id를 입력해주세요.");

                HangmanRound round =roundRepository.getByRoundId(input);
                InputHelper.printInfo(round.resultString());
                break;
            }catch(NoSuchElementException e){
                InputHelper.printInfo(e.getMessage());
            }
        }
        return null;
    }
}
