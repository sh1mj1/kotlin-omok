package woowacourse.omok.domain.model

import woowacourse.omok.domain.model.state.Finished
import woowacourse.omok.domain.model.state.GameState
import woowacourse.omok.domain.model.state.InitialGameTurn
import woowacourse.omok.domain.model.state.InvalidPositionState
import woowacourse.omok.domain.model.state.Running

class OmokGame(
    private val board: Board,
) {
    private var currentGameTurn: GameState = InitialGameTurn()

    fun gameTurn(
        nextPosition: (GameState) -> Position,
        handling: (StonePosition, InvalidPositionState) -> Unit,
        nextStonePositionCallback: (GameState) -> Unit,
        finishedResultCallback: (GameState) -> Unit,
    ): GameState {
        val position = nextPosition(currentGameTurn)

        when (currentGameTurn) {
            is Running -> {
                currentGameTurn = currentGameTurn.place(board, position)

                if(currentGameTurn is InvalidPositionState) return handleInvalidPosition(handling)

                nextStonePositionCallback(currentGameTurn)

                if(currentGameTurn is Finished) return finish(finishedResultCallback)

                return currentGameTurn
            }

            is Finished -> return finish(finishedResultCallback)
            is InvalidPositionState -> return handleInvalidPosition(handling)
        }
    }

    private fun handleInvalidPosition(handling: (StonePosition, InvalidPositionState) -> Unit): GameState {
        currentGameTurn = currentGameTurn.handleInvalidPosition(handling)
        return currentGameTurn
    }

    private fun finish(finishedResultCallback: (GameState) -> Unit): GameState {
        finishedResultCallback(currentGameTurn)
        return currentGameTurn
    }
}
