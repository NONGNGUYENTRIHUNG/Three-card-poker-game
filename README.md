# Three Card Poker

CS 342 Project 2 - Nguyen Tri Hung Nong

## Run the project

Install **JDK 25** and **Maven 3.9 or newer**. Set JAVA_HOME to the JDK 25 folder, and check `java -version` and `mvn -version`. Internet access is needed on the first build to download dependencies. JavaFX 25.0.2 is already configured in pom.xml; no separate JavaFX SDK or IDE settings are needed.

Open a terminal in this folder (the folder containing pom.xml):

```sh
mvn clean test
mvn javafx:run
```

On Windows, extract the ZIP first. Open the project folder in IntelliJ or another IDE as a Maven project, select JDK 25, and run the same commands in its terminal. Do not run the files from inside the ZIP.

## How to play

Start Game begins a new two-player session. Set an Ante from $5 to $25 for each player. Turn Pair Plus on to wager $5-$25, or leave it off for $0. Deal Cards locks bets and shows both player hands. Each player chooses Play or Fold independently against the dealer. Fold asks for confirmation and forfeits Ante and Pair Plus. Play places a wager equal to Ante. The dealer is revealed after both decisions.

Results lists each hand classification, dealer qualification, wager amounts, net gains/losses, returned stakes, any carried Ante, and cumulative net winnings. Another Round preserves cumulative winnings. Main Menu and closing the window ask before ending a session and discarding its totals or held Ante.

Switch Theme changes between a green/gold table and a light blue theme. Tab moves focus; Enter/Space activates controls. Screens scroll if the window is too small. Card accessible text includes rank and suit.

## Components

- `model/Card`: immutable suit and value, 2-14.
- `model/Deck`: initializes, shuffles, draws, and resets 52 distinct cards.
- `model/Dealer`: owns a Deck and deals three-card hands.
- `model/Player`: keeps a hand, wagers, decision, cumulative net winnings, and held Ante.
- `logic/ThreeCardLogic`: pure hand ranking, comparisons, qualification, and Pair Plus profits.
- `service/GameService`: validates wagers and controls BETTING -> DECISIONS -> RESULTS. It settles each player once and prepares the next round.
- `service/RoundResult`: immutable settlement breakdown for the Results screen.
- `controller/*Controller`: event handlers and presentation updates. No hand evaluation, shuffling, or payout calculations live in controllers.
- `ui/Navigator`: FXML loading, shared GameService, session confirmation, themes, help, and screen transitions.
- `ui/CardView`: card labels and visual animations only.

The three FXML files define Welcome, Game Play, and Results. Betting and Play/Fold are two states of the Game Play interface. `docs/UML_Class_Diagram.pdf` describes this implementation.

## Rules and conventions to confirm with the instructor

The supplied write-up refers to the original project's method return conventions and carried-Ante rules, but does not include that original specification. This version uses these explicit conventions:

- `evalHand`: 0 high card, 1 straight flush, 2 three of a kind, 3 straight, 4 flush, 5 pair.
- `compareHands(dealer, player)`: 0 tie, 1 dealer wins, 2 player wins.
- `evalPPWinnings`: winning profit only, excluding returned stake; 0 if the hand does not win Pair Plus. GameService applies the lost Pair Plus stake separately.
- Ace may be low in A-2-3 (lowest straight), or high in Q-K-A (highest straight). K-A-2 is not a straight. Suits do not break ties. Paired hands compare pair value and then kicker; unpaired hands compare high to low.
- A qualified dealer pays Ante and Play 1:1 on a player win. A tie returns both. Pair Plus is independent of the dealer for nonfolded hands.
- If the dealer does not qualify, Play returns and the existing Ante carries to the next round at the same amount. It is locked, without charging another Ante. It stays unresolved until a later qualified comparison or fold. Pair Plus settles every round, even when the dealer does not qualify. A folded player's Ante never carries.
- Cumulative winnings are **net settled profit/loss**, not a bankroll. Original returned stakes are displayed separately. Held Ante is neither profit nor loss yet. There is no bankroll limit in the assignment.

Confirm these conventions against any earlier handout before final submission. They are isolated in ThreeCardLogic and GameService so they can be adjusted.

## Animations

1. Card slide: signals that a hand was dealt.
2. Dealer reveal: collapses then expands the hidden card row to expose the hand after both decisions.
3. Total pulse: emphasizes the updated cumulative result.

Animations provide feedback, but the phase text, decisions, cards, and totals also explain every event without depending on motion. Actions are locked during dealer reveal to avoid conflicting navigation.

## Tests

JUnit Jupiter tests cover all hand categories, straight boundaries, kicker comparisons, ties, Pair Plus payouts, qualification, input validity, deck contents, exhaustion, shuffling, independent deals, and round transitions. Scripted dealers give settlement tests known hands without depending on random outcomes. Tests do not launch JavaFX. Run `mvn test`.

## Design reflection

1. **Why separate rules from controllers?** I want to test the rules without opening the window. The controllers should deal with button clicks and labels, while the model and service handle the game.
2. **What usability problems did students identify?** Pending the real Part 1 classroom testing. This version does not claim that peer testing has already happened.
3. **Which revisions most improved the experience?** Pending classroom feedback and actual before/after revisions. The current design uses clear phase prompts, labeled wager amounts, disabled unavailable actions, and a fold confirmation.
4. **How do animations help?** They show when cards arrive, when the dealer becomes visible, and when winnings update.
5. **How could the interface change without changing rules?** I could replace the FXML layouts and CSS and keep the same model, logic, and GameService calls.
6. **How does the architecture help testing and maintenance?** Each component has a smaller job. I can change a payout rule in the logic/service layer and check it with tests without editing every screen.

## Before final submission

- Complete actual usability sessions with two students outside the team.
- Apply at least two meaningful revisions based on at least three observed findings.
- Update reflection answers 2 and 3 and the FXML designs to match those results.
- Confirm the original return-value and carried-Ante conventions above.
- Run `mvn clean test` and `mvn javafx:run` on the submission computer.
- Keep the UML diagram current if classes or public APIs change.

## Scope review

Switch Theme is required by Section 13. The application uses exactly three purposeful animation types, meeting Section 12: dealing, dealer reveal, and updated totals. A motion-setting checkbox and a fourth screen-fade animation were removed to keep the interface within the requested scope.

Dependencies are limited to JavaFX Controls, JavaFX FXML, and JUnit Jupiter, all required by the assignment. Other imports come from the Java standard library. No external game engine, UI framework, or downloaded card-art library is used.

Validation: Java 25 / JavaFX 25.0.2 build and 66 automated tests pass. A headless UI check exercised the FXML screens and game flow. Its display adapter produced rendering errors during animation, so check the animated visuals with `mvn javafx:run` on your computer.
