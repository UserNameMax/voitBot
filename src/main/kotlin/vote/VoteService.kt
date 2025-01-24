package vote

abstract class VoteService {
    abstract var userPower: Int
    abstract var votes: MutableMap<ULong, Int>
    fun vote(variant: ULong) {
        if (userPower > 0) {
            --userPower
            votes[variant] = votes.getOrDefault(variant, 0) + 1
        }
    }
}