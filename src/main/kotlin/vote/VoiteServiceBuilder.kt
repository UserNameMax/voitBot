package vote

import resources.R
import tech.ydb.auth.iam.CloudAuthHelper
import tech.ydb.core.grpc.GrpcTransport
import tech.ydb.table.SessionRetryContext
import tech.ydb.table.TableClient

fun voteService(userId: Int) = YdbVoteService(user = userId, sessionRetryContext = sessionRetryContext())

fun sessionRetryContext(): SessionRetryContext {
    val transport: GrpcTransport =
        GrpcTransport.forConnectionString(R.connectionString)
            .withAuthProvider(CloudAuthHelper.getServiceAccountJsonAuthProvider(R.authKeyJson))
            .build()
    val tabletClient = TableClient.newClient(transport).build()
    return SessionRetryContext.create(tabletClient).build()
}