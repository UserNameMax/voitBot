package vote

import executeQuery
import tech.ydb.table.SessionRetryContext
import ydb.VotesYdbMap

class YdbVoteService(val user: Int, private val sessionRetryContext: SessionRetryContext) : VoteService() {
    override var userPower: Int
        get() = getPower()
        set(value) {
            setPower(value)
        }
    override var votes: MutableMap<ULong, Int> = VotesYdbMap(sessionRetryContext)

    private fun getPower(): Int {
        val result = mutableSetOf<Int>()
        sessionRetryContext.executeQuery("select value from UserPower where user=$user")
            .getResultSet(0).apply {
                while (next()) {
                    result.add(getColumn("value").uint64.toInt())
                }
            }
        return result.firstOrNull() ?: createUserPower()
    }

    private fun setPower(value: Int) {
        getPower()
        sessionRetryContext.executeQuery("update UserPower set value=$value where user=$user")
    }

    private fun createUserPower(): Int {
        sessionRetryContext.executeQuery("insert into UserPower(user,value) values ($user,10)")
        return 10
    }


}

