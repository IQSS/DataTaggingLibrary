package models

case class CellItem( title: String, infoTitle:String, info: String, severity:Int )
case class Row( title: String, tagName: String, cells:Seq[CellItem] )

object TagsTable {
  private def makeRows = {
    val duaNone = CellItem("None", "None", "Data are released under the CC-BY license. No steps are needed in order to obtain permission to access the data.", 0)
    val duaClickThrough = CellItem("Click Through", "Click Through", "In order to obtain permission to access the data, users go through an on-line process.", 1)
    val duaSign = CellItem("Sign", "Signed Agreement", "Users have to sign an agreement (offline) in order to get a permission to use the data.", 3)

    val encClear = CellItem("Clear", "Clear", "No encryption is used. Fast, cheap, efficient, but data is readable to anyone in the way (service providers, etc.)",0)
    val encEncrypted = CellItem("Encrypted", "(Single) Encryption", "Data is encrypted once, using the server's key. Data is readable only to the server.",1)
    val encDouble = CellItem("Double Encryption", "Double Encryption", "Data is encrypted twice: first using the client key, then using the server's key. Data is readable to the user only (and not the server).",3)

    val authNone = CellItem("None", "None", "No authentication used - anyone can download the data",0)
    val authEmail = CellItem("Email or OAuth", "Email or OAuth", "Users who got an email with a special link, or are authorized via OAuth, are allowed to download the data",1)
    val authPassword = CellItem("Password", "Password", "Users are authenticated using username and password",2)
    val authTwoFactor = CellItem("Two Factor", "Two Factor", "Users are authenticated using two non-related methods, e.g. password and a text message to a device which is not the one they're using.",3)

    Seq(
      Row("Blue",    "noRisk",     Seq(duaNone,         authNone,      encClear, encClear) ),
      Row("Green",   "minimal",    Seq(duaNone,         authEmail,     encClear, encClear) ),
      Row("Yellow",  "shame",      Seq(duaClickThrough, authPassword,  encEncrypted, encClear) ),
      Row("Orange",  "civil",      Seq(duaSign,         authPassword,  encEncrypted, encEncrypted) ),
      Row("Red",     "criminal",   Seq(duaSign,         authTwoFactor, encEncrypted, encEncrypted) ),
      Row("Crimson", "maxControl", Seq(duaSign,         authTwoFactor, encDouble, encDouble) )
    )    
  }

  val rows = makeRows
}
