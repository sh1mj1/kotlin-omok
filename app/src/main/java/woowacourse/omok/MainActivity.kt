package woowacourse.omok

import android.os.Bundle
import android.widget.ImageView
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import woowacourse.omok.domain.model.Board
import woowacourse.omok.domain.model.OmokGame
import woowacourse.omok.domain.model.Position
import woowacourse.omok.domain.model.Stone
import woowacourse.omok.domain.model.StonePosition
import woowacourse.omok.domain.model.database.OmokTurnDao
import woowacourse.omok.domain.model.database.OmokTurnDbHelper
import woowacourse.omok.domain.model.database.toStonePosition
import woowacourse.omok.domain.model.toOmokTurn
import woowacourse.omok.domain.view.output

class MainActivity : AppCompatActivity() {
    private lateinit var omokTurnDao: OmokTurnDao
    private lateinit var boardView: TableLayout

    private lateinit var omokBoard: Board
    private lateinit var omokGame: OmokGame

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        omokBoard = Board()
        omokGame = OmokGame(omokBoard)
        omokTurnDao = OmokTurnDao(OmokTurnDbHelper(this))
        initBoard(omokTurnDao.findAll().map { it.toStonePosition() })
    }

    private fun initBoard(loadedStonePositions: List<StonePosition>) {
        boardView = findViewById(R.id.board)
        boardView
            .children
            .filterIsInstance<TableRow>()
            .forEachIndexed { row, views ->
                views.children.filterIsInstance<ImageView>()
                    .forEachIndexed { col, view ->
                        val currentPosition = Position(row, col)
                        loadBoardData(loadedStonePositions, currentPosition, view)
                        placeStoneListener(view, currentPosition)
                    }
            }
    }

    private fun loadBoardData(
        stonePositions: List<StonePosition>,
        position: Position,
        view: ImageView,
    ) {
        stonePositions.find { it.position == position }?.let {
            changeStoneUI(view, it.stone)
        }
    }

    private fun placeStoneListener(
        view: ImageView,
        currentPosition: Position,
    ) {
        view.setOnClickListener {
            omokGame.gameTurn(
                nextPosition = { currentPosition },
                handling = { stonePosition ->
                    Toast.makeText(
                        this,
                        "${stonePosition.stone.output()}이 두려는 위치는 위치${stonePosition.position.output()}는 ",
                        Toast.LENGTH_SHORT,
                    ).show()
                },
                nextStonePositionCallback = { gameState ->
                    val latestStone = gameState.latestStone()
                    changeStoneUI(view, latestStone)
                    omokTurnDao.save(StonePosition(currentPosition, latestStone).toOmokTurn())
                },
                finishedResultCallback = { gameState ->
                    val latestStone = gameState.latestStone()
                    Toast.makeText(this, "${latestStone.output()}이 승리했습니다.", Toast.LENGTH_SHORT).show()
                    omokTurnDao.clearAll()
                },
            )
            return@setOnClickListener
        }
    }

    private fun changeStoneUI(
        view: ImageView,
        recentPlayerStone: Stone,
    ) {
        view.setImageResource(
            when (recentPlayerStone) {
                Stone.BLACK -> R.drawable.black_stone
                Stone.WHITE -> R.drawable.white_stone
                Stone.NONE -> throw IllegalArgumentException("NONE은 둘 수 없습니다.")
            },
        )
    }
}
