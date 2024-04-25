import java.io.Serializable

class Chats: Serializable {
    var id : String = ""
    var userid : Int = -1
    var mentorid : Int = -1
    var messagecount : Int = 0
    var mentorname: String = ""
    var mentorimg: String = ""
    var username: String = ""
    var userimg: String = ""
}