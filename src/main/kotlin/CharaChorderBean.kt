import com.opencsv.bean.CsvBindByName;

class CharaChorderBean {
    @CsvBindByName
    var freq:String?=null
    @CsvBindByName(column = "Input", required = false)
    var input:String?=null
    @CsvBindByName(column = "Output (word/lemma)")
    var output:String?=null
    @CsvBindByName(column = "humanChecked")
     val humanChecked: Boolean?=null
    @CsvBindByName
     val Alt: String?=null
    @CsvBindByName
     val Ordered: String?=null
    @CsvBindByName
     val Auto: String?=null
    @CsvBindByName
     val Conflict: Int?=null
    @CsvBindByName
     val l_pinky: String?=null
    @CsvBindByName
     val l_ring: String?=null
    @CsvBindByName
     val l_middle: String?=null
    @CsvBindByName
     val l_index: String?=null
    @CsvBindByName
     val lt1: String?=null
    @CsvBindByName
     val lt2: String?=null
    @CsvBindByName
     val rt1: String?=null
    @CsvBindByName
     val rt2: String?=null
    @CsvBindByName
     val r_index: String?=null
    @CsvBindByName
     val r_middle: String?=null
    @CsvBindByName
     val r_ring: String?=null
    @CsvBindByName
     val r_pinky: String?=null
    @CsvBindByName(column = "1st")
     val first: String?=null
    @CsvBindByName(column = "2nd")
     val second: String?=null
    @CsvBindByName(column = "3rd")
     val third: String?=null
}