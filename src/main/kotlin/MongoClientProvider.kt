import com.mongodb.MongoClient
import com.mongodb.MongoException

//FIXME, small wrapper, question is, do I really need it perhaps some extension function exressions to wrap the API
class MongoClientProvider {
    private var mongoClient: MongoClient? = null

    fun connect(url: String, port: Int) {
        try {
            mongoClient = MongoClient(url, port)
            LOG.debug("Connecting to MongoDB")
        } catch (e: MongoException) {
            LOG.debug("Mongo DB connection failed...")
            e.printStackTrace()
        }
    }

    fun getDb(name: String) = mongoClient?.getDatabase(name)
    fun close() {
        mongoClient?.close()
    }
}