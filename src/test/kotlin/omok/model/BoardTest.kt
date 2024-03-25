package omok.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class BoardTest {
    private lateinit var emptyBoard: Board
    private lateinit var player: Player
    private val playerStone = Stone.BLACK

    @BeforeEach
    fun setUp() {
        emptyBoard = Board()
        player = Player(playerStone)
    }

    @Test
    fun `오목판에 돌을 놓는다`() {
        emptyBoard.place(Position(3, 3), player)

        val actual = emptyBoard.find(Position(3, 3))
        assertThat(actual).isEqualTo(playerStone)
    }

    @Test
    fun `오목판에 이미 돌이 있는 곳에 놓으면 예외가 발생한다`() {
        val board =
            initBoard(
                StonePosition(Position(3, 3), playerStone),
            )
        assertThrows<IllegalArgumentException> { board.place(Position(3, 3), player) }
    }
}
