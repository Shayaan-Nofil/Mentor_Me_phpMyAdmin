import java.io.Serializable

class Mentors: Serializable {
    var id: String = ""
    var name: String = ""
    var email: String = ""
    var password: String = ""
    var job: String = ""
    var description: String = ""
    var rating: Double = 0.0
    var profilepic: String = ""
    var rate: Int = 0
    var status: String = ""
    var token: String = ""

    fun addData (ID:String = "", nam:String = "", jb: String = "" , descp: String = "", pic: String = "" , ratg: Double = 0.0, rt: Int = 0){
        name = nam
        job = jb
        description = descp
        profilepic = pic
        rating = ratg
        rate = rt
    }
}