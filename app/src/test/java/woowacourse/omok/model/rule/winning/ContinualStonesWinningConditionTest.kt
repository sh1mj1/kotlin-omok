package woowacourse.omok.model.rule.winning

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import woowacourse.omok.domain.model.ContinualStonesCondition
import woowacourse.omok.domain.model.Position
import woowacourse.omok.domain.model.Stone
import woowacourse.omok.domain.model.StonePosition
import woowacourse.omok.domain.model.rule.ContinualStonesStandard
import woowacourse.omok.domain.model.rule.winning.ContinualStonesWinningCondition
import woowacourse.omok.model.initBoard

class ContinualStonesWinningConditionTest {
    @Test
    fun `연속 돌 기준이 사목으로 지정되어 있다면 Double 금수 규칙(3-3, 4-4) 을 지정할 수 없다`() {
        val continualStonesWinningCondition =
            ContinualStonesWinningCondition(
                ContinualStonesStandard(4),
                ContinualStonesCondition.STRICT,
            )
        assertThat(continualStonesWinningCondition.canHaveDoubleRule()).isFalse
    }

    @Test
    fun `우승 기준이 N목 이상으로 지정되어 있다면 장목 금수 규칙을 지정할 수 없다`() {
        val continualStonesWinningCondition =
            ContinualStonesWinningCondition(
                ContinualStonesStandard(5),
                ContinualStonesCondition.OVERLINE_AVAILABLE,
            )
        assertThat(continualStonesWinningCondition.canHaveOverlineRule()).isFalse
    }

    @ParameterizedTest
    @ValueSource(strings = ["BLACK", "WHITE"])
    fun `장목 비허용 & 오목 우승일 경우, 정확히 오목이 되면 승리한다`(stoneName: String) {
        // given
        val playerStone = Stone.valueOf(stoneName)
        val winningCondition =
            ContinualStonesWinningCondition(
                continualStonesStandard = ContinualStonesStandard(5),
                continualStonesCondition = ContinualStonesCondition.STRICT,
            )

        val board =
            initBoard(
                StonePosition(Position(3, 3), playerStone),
                StonePosition(Position(3, 4), playerStone),
                StonePosition(Position(3, 5), playerStone),
                StonePosition(Position(3, 6), playerStone),
                StonePosition(Position(3, 7), playerStone),
            )

        // when
        val actual = winningCondition.isWin(board, Position(3, 7))

        // then
        assertThat(actual).isTrue
    }

    @ParameterizedTest
    @ValueSource(strings = ["BLACK", "WHITE"])
    fun `장목 허용 & 오목 우승일 경우, 육목이 되면 승리하지 않는다`(stoneName: String) {
        // given
        val playerStone = Stone.valueOf(stoneName)
        val winningCondition =
            ContinualStonesWinningCondition(
                continualStonesStandard = ContinualStonesStandard(5),
                continualStonesCondition = ContinualStonesCondition.STRICT,
            )

        val board =
            initBoard(
                StonePosition(Position(3, 3), playerStone),
                StonePosition(Position(3, 4), playerStone),
                StonePosition(Position(3, 5), playerStone),
                StonePosition(Position(3, 6), playerStone),
                StonePosition(Position(3, 7), playerStone),
                StonePosition(Position(3, 8), playerStone),
            )

        // when
        val actual = winningCondition.isWin(board, Position(3, 7))

        // then
        assertThat(actual).isFalse
    }
}
