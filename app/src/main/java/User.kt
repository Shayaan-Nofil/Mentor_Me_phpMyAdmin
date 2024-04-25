import java.io.Serializable

class User: Serializable {
    var id: String = ""
    var name:String = ""
    var email: String = ""
    var password: String = ""
    var number: String = ""
    var city: String = ""
    var  country: String = ""
    var token: String = ""
    var profilepic: String = ""
    var bgpic: String = ""
    fun addData (ID:String = "", nam:String = "", eml: String = "" , num: String = "", cty: String = "" , ctr: String){
        name = nam
        email = eml
        number = num
        city = cty
        country = ctr
    }
}