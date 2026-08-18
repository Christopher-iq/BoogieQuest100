package com.biblequest.pro

import android.media.AudioManager
import android.media.ToneGenerator
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

private val Bg = Color(0xFF08111F)
private val Panel = Color(0xD9192A3D)
private val Panel2 = Color(0xFF203650)
private val Gold = Color(0xFFF5C451)
private val Sky = Color(0xFF49C9FF)
private val Mint = Color(0xFF70E7B6)
private val Ink = Color(0xFFFFF8E7)
private val Muted = Color(0xFFB7C6D8)
private val Bad = Color(0xFFFF7486)

data class Q(
    val category: String,
    val difficulty: String,
    val en: String,
    val hi: String,
    val enOptions: List<String>,
    val hiOptions: List<String>,
    val correct: Int,
    val referenceEn: String,
    val referenceHi: String,
    val explainEn: String,
    val explainHi: String
)

private fun q(category:String,difficulty:String,en:String,hi:String,enOptions:List<String>,hiOptions:List<String>,correct:Int,refEn:String,refHi:String,exEn:String,exHi:String)=
    Q(category,difficulty,en,hi,enOptions,hiOptions,correct,refEn,refHi,exEn,exHi)

private val questions = listOf(
    q("Creation","Easy","Who created the heavens and the earth?","आकाश और पृथ्वी की सृष्टि किसने की?",listOf("Moses","God","Adam","Noah"),listOf("मूसा","परमेश्वर","आदम","नूह"),1,"Genesis 1:1","उत्पत्ति 1:1","Scripture opens by identifying God as Creator of the heavens and the earth.","पवित्रशास्त्र की शुरुआत परमेश्वर को आकाश और पृथ्वी के सृष्टिकर्ता के रूप में बताती है।"),
    q("Creation","Easy","On which day were human beings created?","मनुष्य की सृष्टि किस दिन हुई?",listOf("Third","Fourth","Fifth","Sixth"),listOf("तीसरे","चौथे","पाँचवें","छठे"),3,"Genesis 1:26-31","उत्पत्ति 1:26-31","Human beings were created on the sixth day.","मनुष्य की सृष्टि छठे दिन हुई।"),
    q("Patriarchs","Easy","Who built the ark?","जहाज़ किसने बनाया?",listOf("Abraham","Noah","Isaac","Jacob"),listOf("अब्राहम","नूह","इसहाक","याकूब"),1,"Genesis 6:13-22","उत्पत्ति 6:13-22","God instructed Noah to build the ark before the flood.","जलप्रलय से पहले परमेश्वर ने नूह को जहाज़ बनाने की आज्ञा दी।"),
    q("Patriarchs","Easy","Who was Abraham's promised son?","अब्राहम का प्रतिज्ञा किया हुआ पुत्र कौन था?",listOf("Ishmael","Isaac","Esau","Joseph"),listOf("इश्माएल","इसहाक","एसाव","यूसुफ"),1,"Genesis 21:1-3","उत्पत्ति 21:1-3","Isaac was born to Abraham and Sarah according to God's promise.","परमेश्वर की प्रतिज्ञा के अनुसार अब्राहम और सारा से इसहाक उत्पन्न हुआ।"),
    q("Patriarchs","Medium","What new name was given to Jacob?","याकूब को नया नाम क्या दिया गया?",listOf("Judah","Israel","Ephraim","Benjamin"),listOf("यहूदा","इस्राएल","एप्रैम","बिन्यामीन"),1,"Genesis 32:28","उत्पत्ति 32:28","Jacob received the name Israel after wrestling.","मल्लयुद्ध के बाद याकूब को इस्राएल नाम दिया गया।"),
    q("Exodus","Easy","Who led Israel out of Egypt?","इस्राएलियों को मिस्र से कौन बाहर ले गया?",listOf("Aaron","Joshua","Moses","Caleb"),listOf("हारून","यहोशू","मूसा","कालेब"),2,"Exodus 3:10","निर्गमन 3:10","God called Moses to bring Israel out of Egypt.","परमेश्वर ने मूसा को इस्राएलियों को मिस्र से निकालने के लिए बुलाया।"),
    q("Exodus","Easy","Which sea did Israel cross on dry ground?","इस्राएलियों ने सूखी भूमि पर किस समुद्र को पार किया?",listOf("Dead Sea","Sea of Galilee","Red Sea","Mediterranean"),listOf("मृत सागर","गलील की झील","लाल समुद्र","भूमध्य सागर"),2,"Exodus 14:21-22","निर्गमन 14:21-22","The waters divided and Israel crossed through on dry ground.","जल दो भागों में बँट गया और इस्राएली सूखी भूमि पर होकर पार गए।"),
    q("Law","Easy","Where did Moses receive the Ten Commandments?","मूसा ने दस आज्ञाएँ कहाँ प्राप्त कीं?",listOf("Carmel","Sinai","Zion","Nebo"),listOf("कर्मेल","सीनै","सिय्योन","नबो"),1,"Exodus 19-20","निर्गमन 19-20","God gave the covenant commandments at Mount Sinai.","परमेश्वर ने सीनै पर्वत पर वाचा की आज्ञाएँ दीं।"),
    q("Conquest","Easy","Who succeeded Moses as Israel's leader?","मूसा के बाद इस्राएल का अगुवा कौन बना?",listOf("Samuel","Joshua","Gideon","Saul"),listOf("शमूएल","यहोशू","गिदोन","शाऊल"),1,"Joshua 1:1-2","यहोशू 1:1-2","After Moses' death, Joshua was commissioned to lead Israel.","मूसा की मृत्यु के बाद यहोशू को इस्राएल का नेतृत्व सौंपा गया।"),
    q("Judges","Easy","Which judge was famous for extraordinary strength?","कौन-सा न्यायी असाधारण शक्ति के लिए प्रसिद्ध था?",listOf("Gideon","Samson","Jephthah","Ehud"),listOf("गिदोन","शिमशोन","यिप्तह","एहूद"),1,"Judges 13-16","न्यायियों 13-16","Samson's unusual strength was connected with his Nazirite calling.","शिमशोन की असाधारण शक्ति उसके नाज़ीर समर्पण से जुड़ी थी।"),
    q("Kings","Easy","Who defeated Goliath?","गोलियत को किसने हराया?",listOf("Saul","David","Jonathan","Samuel"),listOf("शाऊल","दाऊद","योनातान","शमूएल"),1,"1 Samuel 17:45-50","1 शमूएल 17:45-50","David trusted God and defeated Goliath with a sling and stone.","दाऊद ने परमेश्वर पर भरोसा किया और गोफन व पत्थर से गोलियत को हराया।"),
    q("Kings","Easy","Who was Israel's first king?","इस्राएल का पहला राजा कौन था?",listOf("David","Solomon","Saul","Samuel"),listOf("दाऊद","सुलैमान","शाऊल","शमूएल"),2,"1 Samuel 10:1","1 शमूएल 10:1","Samuel anointed Saul as king.","शमूएल ने शाऊल का राजा के रूप में अभिषेक किया।"),
    q("Kings","Easy","Which king asked God for wisdom?","किस राजा ने परमेश्वर से बुद्धि माँगी?",listOf("David","Solomon","Hezekiah","Josiah"),listOf("दाऊद","सुलैमान","हिजकिय्याह","योशिय्याह"),1,"1 Kings 3:5-12","1 राजा 3:5-12","Solomon asked for an understanding heart to govern well.","सुलैमान ने लोगों का न्याय करने के लिए समझने वाला मन माँगा।"),
    q("Prophets","Easy","Which prophet was swallowed by a great fish?","कौन-सा भविष्यद्वक्ता बड़ी मछली द्वारा निगल लिया गया?",listOf("Isaiah","Jeremiah","Jonah","Amos"),listOf("यशायाह","यिर्मयाह","योना","आमोस"),2,"Jonah 1:17","योना 1:17","After fleeing God's call, Jonah was swallowed by a great fish.","परमेश्वर की बुलाहट से भागने के बाद योना को बड़ी मछली ने निगल लिया।"),
    q("Prophets","Medium","Who challenged the prophets of Baal on Mount Carmel?","किसने कर्मेल पर्वत पर बाल के भविष्यद्वक्ताओं को चुनौती दी?",listOf("Elisha","Elijah","Isaiah","Hosea"),listOf("एलीशा","एलिय्याह","यशायाह","होशे"),1,"1 Kings 18:20-39","1 राजा 18:20-39","Elijah called Israel to choose whom they would serve; God answered by fire.","एलिय्याह ने इस्राएल को निर्णय के लिए बुलाया और परमेश्वर ने आग से उत्तर दिया।"),
    q("Wisdom","Easy","Which Psalm says, 'The Lord is my shepherd'?","कौन-सा भजन कहता है, 'यहोवा मेरा चरवाहा है'?",listOf("Psalm 1","Psalm 23","Psalm 51","Psalm 150"),listOf("भजन 1","भजन 23","भजन 51","भजन 150"),1,"Psalm 23:1","भजन 23:1","Psalm 23 presents the Lord as a caring shepherd.","भजन 23 प्रभु को देखभाल करने वाले चरवाहे के रूप में दिखाता है।"),
    q("Wisdom","Medium","According to Proverbs, what is the beginning of wisdom?","नीतिवचन के अनुसार बुद्धि का आरम्भ क्या है?",listOf("Knowledge","Fear of the Lord","Wealth","Silence"),listOf("ज्ञान","यहोवा का भय","धन","मौन"),1,"Proverbs 9:10","नीतिवचन 9:10","Reverence for the Lord is presented as the foundation of wisdom.","यहोवा के प्रति श्रद्धा को बुद्धि की नींव बताया गया है।"),
    q("Women","Easy","Who was Samuel's mother?","शमूएल की माता कौन थी?",listOf("Ruth","Hannah","Esther","Elizabeth"),listOf("रूत","हन्ना","एस्तेर","इलीशिबा"),1,"1 Samuel 1:20","1 शमूएल 1:20","Hannah prayed for a son and named him Samuel.","हन्ना ने पुत्र के लिए प्रार्थना की और उसका नाम शमूएल रखा।"),
    q("Women","Easy","Who became queen and helped save the Jewish people in Persia?","कौन रानी बनी और फारस में यहूदी लोगों को बचाने में सहायता की?",listOf("Ruth","Esther","Deborah","Miriam"),listOf("रूत","एस्तेर","दबोरा","मरियम"),1,"Esther 4-8","एस्तेर 4-8","Esther courageously approached the king on behalf of her people.","एस्तेर ने साहसपूर्वक अपनी प्रजा के लिए राजा के सामने निवेदन किया।"),
    q("Women","Medium","Who was Ruth's mother-in-law?","रूत की सास कौन थी?",listOf("Naomi","Miriam","Hannah","Sarah"),listOf("नाओमी","मरियम","हन्ना","सारा"),0,"Ruth 1:3-5","रूत 1:3-5","Ruth remained loyal to Naomi after their husbands died.","पतियों की मृत्यु के बाद रूत नाओमी के प्रति निष्ठावान रही।"),
    q("Gospels","Easy","Where was Jesus born?","यीशु का जन्म कहाँ हुआ?",listOf("Nazareth","Jerusalem","Bethlehem","Capernaum"),listOf("नासरत","यरूशलेम","बेतलेहेम","कफरनहूम"),2,"Matthew 2:1; Luke 2:4-7","मत्ती 2:1; लूका 2:4-7","Jesus was born in Bethlehem.","यीशु का जन्म बेतलेहेम में हुआ।"),
    q("Gospels","Easy","Who baptized Jesus?","यीशु को किसने बपतिस्मा दिया?",listOf("Peter","John the Baptist","James","Andrew"),listOf("पतरस","यूहन्ना बपतिस्मा देनेवाला","याकूब","अन्द्रियास"),1,"Matthew 3:13-17","मत्ती 3:13-17","John baptized Jesus in the Jordan.","यूहन्ना ने यरदन में यीशु को बपतिस्मा दिया।"),
    q("Gospels","Easy","How many apostles did Jesus appoint?","यीशु ने कितने प्रेरित नियुक्त किए?",listOf("7","10","12","14"),listOf("7","10","12","14"),2,"Mark 3:13-19","मरकुस 3:13-19","Jesus appointed twelve to be with him and to be sent out.","यीशु ने बारह को अपने साथ रहने और भेजे जाने के लिए नियुक्त किया।"),
    q("Gospels","Easy","What did Jesus turn water into at Cana?","काना में यीशु ने पानी को किसमें बदल दिया?",listOf("Oil","Wine","Milk","Honey"),listOf("तेल","दाखरस","दूध","मधु"),1,"John 2:1-11","यूहन्ना 2:1-11","At the wedding in Cana, Jesus turned water into wine.","काना के विवाह में यीशु ने पानी को दाखरस में बदल दिया।"),
    q("Gospels","Easy","Who walked on water toward Jesus?","यीशु की ओर पानी पर कौन चला?",listOf("John","Peter","Thomas","Andrew"),listOf("यूहन्ना","पतरस","थोमा","अन्द्रियास"),1,"Matthew 14:28-31","मत्ती 14:28-31","Peter stepped out of the boat and walked toward Jesus.","पतरस नाव से उतरकर पानी पर यीशु की ओर चला।"),
    q("Gospels","Easy","Who betrayed Jesus?","यीशु को किसने पकड़वाया?",listOf("Peter","Judas Iscariot","Thomas","Matthew"),listOf("पतरस","यहूदा इस्करियोती","थोमा","मत्ती"),1,"Matthew 26:14-16,47-50","मत्ती 26:14-16,47-50","Judas arranged to betray Jesus and identified him to the arresting crowd.","यहूदा ने यीशु को पकड़वाने का समझौता किया और गिरफ्तारी के समय उसकी पहचान कराई।"),
    q("Gospels","Easy","On which day did Jesus rise from the dead?","यीशु मृतकों में से किस दिन जी उठा?",listOf("Sabbath","First day of the week","Friday","Second day"),listOf("सब्त","सप्ताह के पहले दिन","शुक्रवार","दूसरे दिन"),1,"Matthew 28:1-6","मत्ती 28:1-6","After the Sabbath, on the first day of the week, the tomb was found empty.","सब्त के बाद सप्ताह के पहले दिन कब्र खाली पाई गई।"),
    q("Gospels","Medium","Who said, 'You are the Christ, the Son of the living God'?","किसने कहा, 'तू जीवते परमेश्वर का पुत्र मसीह है'?",listOf("John","Peter","Thomas","Nathanael"),listOf("यूहन्ना","पतरस","थोमा","नतनएल"),1,"Matthew 16:16","मत्ती 16:16","Peter confessed Jesus as the Messiah and Son of the living God.","पतरस ने यीशु को मसीह और जीवते परमेश्वर का पुत्र स्वीकार किया।"),
    q("Gospels","Medium","Who climbed a sycamore tree to see Jesus?","यीशु को देखने के लिए गूलर के पेड़ पर कौन चढ़ा?",listOf("Nicodemus","Zacchaeus","Bartimaeus","Jairus"),listOf("नीकुदेमुस","जक्कई","बरतिमाई","याईर"),1,"Luke 19:1-10","लूका 19:1-10","Zacchaeus climbed a tree because the crowd blocked his view.","भीड़ के कारण जक्कई यीशु को नहीं देख पा रहा था, इसलिए वह पेड़ पर चढ़ गया।"),
    q("Gospels","Medium","Who was raised after being dead four days?","चार दिन मृत रहने के बाद किसे जिलाया गया?",listOf("Jairus' daughter","Lazarus","Tabitha","Eutychus"),listOf("याईर की बेटी","लाज़र","तबीता","यूतुखुस"),1,"John 11:39-44","यूहन्ना 11:39-44","Jesus called Lazarus out of the tomb after four days.","चार दिन बाद यीशु ने लाज़र को कब्र से बाहर बुलाया।"),
    q("Acts","Easy","What happened at Pentecost?","पिन्तेकुस्त के दिन क्या हुआ?",listOf("Jesus was born","The temple was built","The Holy Spirit came upon the believers","Paul was arrested"),listOf("यीशु का जन्म हुआ","मंदिर बना","पवित्र आत्मा विश्वासियों पर उतरा","पौलुस गिरफ्तार हुआ"),2,"Acts 2:1-4","प्रेरितों के काम 2:1-4","The disciples were filled with the Holy Spirit at Pentecost.","पिन्तेकुस्त पर चेले पवित्र आत्मा से भर गए।"),
    q("Acts","Medium","Who was the first Christian martyr recorded in Acts?","प्रेरितों के काम में दर्ज पहला मसीही शहीद कौन था?",listOf("James","Stephen","Philip","Barnabas"),listOf("याकूब","स्तिफनुस","फिलिप्पुस","बरनबास"),1,"Acts 7:54-60","प्रेरितों के काम 7:54-60","Stephen was stoned after bearing witness before the council.","सभा के सामने गवाही देने के बाद स्तिफनुस को पत्थरवाह किया गया।"),
    q("Acts","Easy","What was Paul's earlier name?","पौलुस का पहले का नाम क्या था?",listOf("Simon","Saul","Silas","Simeon"),listOf("शमौन","शाऊल","सीलास","शिमोन"),1,"Acts 9:1-4; 13:9","प्रेरितों के काम 9:1-4; 13:9","The persecutor Saul later appears as Paul in the mission narrative.","सताने वाला शाऊल बाद में मिशन की कथा में पौलुस के नाम से प्रकट होता है।"),
    q("Acts","Medium","Who baptized the Ethiopian official?","इथियोपियाई अधिकारी को किसने बपतिस्मा दिया?",listOf("Peter","Philip","Paul","Barnabas"),listOf("पतरस","फिलिप्पुस","पौलुस","बरनबास"),1,"Acts 8:26-39","प्रेरितों के काम 8:26-39","Philip explained Isaiah to the official and baptized him.","फिलिप्पुस ने अधिकारी को यशायाह समझाया और उसे बपतिस्मा दिया।"),
    q("Epistles","Easy","According to 1 Corinthians 13, what is greatest among faith, hope and love?","1 कुरिन्थियों 13 के अनुसार विश्वास, आशा और प्रेम में सबसे बड़ा क्या है?",listOf("Faith","Hope","Love","Knowledge"),listOf("विश्वास","आशा","प्रेम","ज्ञान"),2,"1 Corinthians 13:13","1 कुरिन्थियों 13:13","Paul says the greatest of faith, hope and love is love.","पौलुस कहता है कि विश्वास, आशा और प्रेम में सबसे बड़ा प्रेम है।"),
    q("Epistles","Medium","What is called the 'sword of the Spirit'?","'आत्मा की तलवार' किसे कहा गया है?",listOf("Faith","Prayer","The word of God","Righteousness"),listOf("विश्वास","प्रार्थना","परमेश्वर का वचन","धार्मिकता"),2,"Ephesians 6:17","इफिसियों 6:17","In the armor of God, the sword of the Spirit is the word of God.","परमेश्वर के हथियारों में आत्मा की तलवार परमेश्वर का वचन है।"),
    q("Epistles","Medium","Which fruit of the Spirit is listed first in Galatians 5:22?","गलातियों 5:22 में आत्मा का पहला फल कौन-सा है?",listOf("Joy","Peace","Love","Faithfulness"),listOf("आनन्द","शान्ति","प्रेम","विश्वासयोग्यता"),2,"Galatians 5:22-23","गलातियों 5:22-23","The list begins with love, followed by joy and peace.","सूची प्रेम से शुरू होती है, फिर आनन्द और शान्ति आते हैं।"),
    q("Prophets","Easy","Who was thrown into the lions' den?","सिंहों की मांद में किसे डाला गया?",listOf("Daniel","Jeremiah","Ezekiel","Hosea"),listOf("दानिय्येल","यिर्मयाह","यहेजकेल","होशे"),0,"Daniel 6","दानिय्येल 6","Daniel continued praying to God and was preserved in the lions' den.","दानिय्येल परमेश्वर से प्रार्थना करता रहा और सिंहों की मांद में सुरक्षित रखा गया।"),
    q("Prophets","Medium","Which prophet saw a valley of dry bones?","किस भविष्यद्वक्ता ने सूखी हड्डियों की तराई देखी?",listOf("Isaiah","Ezekiel","Daniel","Zechariah"),listOf("यशायाह","यहेजकेल","दानिय्येल","जकर्याह"),1,"Ezekiel 37:1-14","यहेजकेल 37:1-14","Ezekiel's vision pictured God's power to restore his people.","यहेजकेल का दर्शन परमेश्वर की अपनी प्रजा को पुनर्स्थापित करने की सामर्थ्य का चित्र है।"),
    q("Revelation","Easy","Who received Revelation on Patmos?","पत्मुस द्वीप पर प्रकाशितवाक्य किसने प्राप्त किया?",listOf("Peter","Paul","John","James"),listOf("पतरस","पौलुस","यूहन्ना","याकूब"),2,"Revelation 1:9","प्रकाशितवाक्य 1:9","John says he was on Patmos when he received the vision.","यूहन्ना कहता है कि दर्शन प्राप्त करते समय वह पत्मुस द्वीप पर था।")
)

private val categories = listOf("All","Creation","Patriarchs","Exodus","Law","Conquest","Judges","Kings","Prophets","Wisdom","Women","Gospels","Acts","Epistles","Revelation")

enum class Screen { HOME, CATEGORIES, QUIZ, RESULT, REWARDS, SETTINGS }
data class Player(val xp:Int=0,val coins:Int=60,val streak:Int=1,val correct:Int=0,val answered:Int=0)

@Composable
fun BibleQuestApp() {
    MaterialTheme {
        var screen by remember { mutableStateOf(Screen.HOME) }
        var hindi by remember { mutableStateOf(false) }
        var sound by remember { mutableStateOf(true) }
        var music by remember { mutableStateOf(true) }
        var player by remember { mutableStateOf(Player()) }
        var activeCategory by remember { mutableStateOf("All") }
        var activeQuestions by remember { mutableStateOf(emptyList<Q>()) }
        var score by remember { mutableIntStateOf(0) }
        var title by remember { mutableStateOf("Quick Quest") }
        val tone = remember { ToneGenerator(AudioManager.STREAM_MUSIC, 28) }
        DisposableEffect(Unit) { onDispose { tone.release() } }
        LaunchedEffect(music) {
            while (music) {
                tone.startTone(ToneGenerator.TONE_PROP_BEEP2, 70)
                delay(950)
                tone.startTone(ToneGenerator.TONE_PROP_BEEP, 55)
                delay(1250)
            }
        }
        fun begin(category:String,count:Int,quizTitle:String) {
            activeCategory = category
            title = quizTitle
            val source = if (category == "All") questions else questions.filter { it.category == category }
            activeQuestions = source.shuffled(Random(System.nanoTime())).take(min(count, source.size))
            score = 0
            screen = Screen.QUIZ
        }
        Box(Modifier.fillMaxSize().background(Bg)) {
            Aura()
            AnimatedContent(targetState=screen,transitionSpec={
                (fadeIn()+slideInHorizontally { it/10 }) togetherWith (fadeOut()+slideOutHorizontally { -it/12 })
            },label="screen") { s ->
                when(s) {
                    Screen.HOME -> Home(hindi,player,{screen=Screen.CATEGORIES},{begin("All",10,if(hindi)"त्वरित क्विज़" else "Quick Quest")},{begin("All",7,if(hindi)"दैनिक चुनौती" else "Daily Challenge")},{screen=Screen.REWARDS},{screen=Screen.SETTINGS})
                    Screen.CATEGORIES -> CategoryScreen(hindi,{screen=Screen.HOME}) { begin(it,10,it) }
                    Screen.QUIZ -> QuizScreen(hindi,title,activeQuestions,sound,tone,{ right ->
                        if(right) { score++; player=player.copy(xp=player.xp+10,coins=player.coins+2,correct=player.correct+1,answered=player.answered+1) }
                        else player=player.copy(answered=player.answered+1)
                    },{screen=Screen.RESULT})
                    Screen.RESULT -> ResultScreen(hindi,score,activeQuestions.size,player,{screen=Screen.HOME},{begin(activeCategory,10,title)})
                    Screen.REWARDS -> RewardScreen(hindi,player){screen=Screen.HOME}
                    Screen.SETTINGS -> SettingsScreen(hindi,sound,music,{hindi=!hindi},{sound=!sound},{music=!music}){screen=Screen.HOME}
                }
            }
        }
    }
}

@Composable private fun Aura() {
    Canvas(Modifier.fillMaxSize()) {
        drawCircle(Sky.copy(alpha=.08f),size.minDimension*.4f,Offset(size.width*.04f,size.height*.2f))
        drawCircle(Gold.copy(alpha=.08f),size.minDimension*.33f,Offset(size.width*.94f,size.height*.67f))
        drawCircle(Color(0xFF795FFF).copy(alpha=.09f),size.minDimension*.28f,Offset(size.width*.4f,size.height*.98f))
    }
}

@Composable private fun TopBar(title:String,coins:Int,onBack:(()->Unit)?=null,onSettings:(()->Unit)?=null) {
    Row(Modifier.statusBarsPadding().padding(14.dp).fillMaxWidth().clip(RoundedCornerShape(24.dp)).background(Panel).padding(12.dp),verticalAlignment=Alignment.CenterVertically) {
        if(onBack!=null) Text("‹",Modifier.clickable(onClick=onBack).padding(8.dp),color=Ink,fontSize=30.sp,fontWeight=FontWeight.Bold)
        Column(Modifier.weight(1f)) {
            Text("BIBLE QUEST PRO",fontSize=9.sp,letterSpacing=2.sp,color=Gold,fontWeight=FontWeight.Black)
            Text(title,color=Ink,fontWeight=FontWeight.Bold,fontSize=19.sp)
        }
        Surface(color=Panel2,shape=RoundedCornerShape(18.dp)) { Text("✦ $coins",Modifier.padding(horizontal=11.dp,vertical=8.dp),color=Gold,fontWeight=FontWeight.Black) }
        if(onSettings!=null) Text("⚙",Modifier.clickable(onClick=onSettings).padding(9.dp),fontSize=20.sp,color=Ink)
    }
}

@Composable private fun Glass(modifier:Modifier=Modifier,content:@Composable ColumnScope.()->Unit) {
    Surface(modifier,color=Panel,shape=RoundedCornerShape(26.dp),border=BorderStroke(1.dp,Color.White.copy(alpha=.09f)),shadowElevation=10.dp) {
        Column(Modifier.padding(18.dp),content=content)
    }
}

@Composable private fun HeroButton(text:String,onClick:()->Unit) {
    Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(19.dp)).background(Brush.linearGradient(listOf(Gold,Sky))).clickable(onClick=onClick).padding(16.dp),contentAlignment=Alignment.Center) {
        Text(text,color=Bg,fontWeight=FontWeight.Black,fontSize=16.sp)
    }
}

@Composable private fun Home(hi:Boolean,p:Player,onTopics:()->Unit,onQuick:()->Unit,onDaily:()->Unit,onRewards:()->Unit,onSettings:()->Unit) {
    Column(Modifier.fillMaxSize()) {
        TopBar(if(hi)"सीखें • खेलें • बढ़ें" else "Learn • Play • Grow",p.coins,onSettings=onSettings)
        LazyColumn(contentPadding=PaddingValues(18.dp,8.dp,18.dp,30.dp),verticalArrangement=Arrangement.spacedBy(14.dp)) {
            item { Glass(Modifier.fillMaxWidth()) {
                Row(verticalAlignment=Alignment.CenterVertically) {
                    Box(Modifier.size(74.dp).clip(RoundedCornerShape(23.dp)).background(Brush.linearGradient(listOf(Gold,Sky))),contentAlignment=Alignment.Center) { Text("✝",fontSize=40.sp,color=Bg,fontWeight=FontWeight.Black) }
                    Spacer(Modifier.size(14.dp))
                    Column { Text(if(hi)"आज का वचन" else "VERSE OF THE DAY",color=Gold,fontSize=10.sp,fontWeight=FontWeight.Black);Text(if(hi)"“तेरा वचन मेरे पाँव के लिए दीपक है।”" else "“Your word is a lamp for my feet.”",color=Ink,fontSize=17.sp,fontWeight=FontWeight.Bold);Text(if(hi)"भजन 119:105" else "Psalm 119:105",color=Muted,fontSize=12.sp) }
                }
            }}
            item { Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(8.dp)) { Stat("${p.xp}","XP",Modifier.weight(1f));Stat("🔥 ${p.streak}",if(hi)"श्रृंखला" else "STREAK",Modifier.weight(1f));Stat("${p.correct}/${max(1,p.answered)}",if(hi)"सही" else "RIGHT",Modifier.weight(1f)) } }
            item { HeroButton(if(hi)"⚡ त्वरित क्विज़" else "⚡ QUICK QUEST",onQuick) }
            item { Glass(Modifier.fillMaxWidth()) { Text(if(hi)"आज की चुनौती" else "DAILY CHALLENGE",color=Gold,fontSize=10.sp,fontWeight=FontWeight.Black);Text(if(hi)"7 प्रश्न • बोनस XP • दैनिक इनाम" else "7 questions • bonus XP • daily reward",color=Ink,fontSize=18.sp,fontWeight=FontWeight.Bold);Spacer(Modifier.height(10.dp));OutlinedButton(onClick=onDaily,modifier=Modifier.fillMaxWidth()){Text(if(hi)"शुरू करें" else "PLAY TODAY",color=Sky)} } }
            item { Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(10.dp)) { MenuTile("📚",if(hi)"विषय" else "TOPICS",onTopics,Modifier.weight(1f));MenuTile("🏆",if(hi)"इनाम" else "REWARDS",onRewards,Modifier.weight(1f)) } }
            item { Glass(Modifier.fillMaxWidth()) { Text(if(hi)"सीखने का तरीका" else "LEARNING LOOP",color=Gold,fontSize=10.sp,fontWeight=FontWeight.Black);Text(if(hi)"उत्तर दें → कारण समझें → अध्याय/पद देखें → XP और सिक्के कमाएँ।" else "Answer → understand why → see the chapter/verse → earn XP and coins.",color=Muted) } }
        }
    }
}

@Composable private fun Stat(v:String,l:String,m:Modifier){Glass(m){Text(v,color=Ink,fontSize=19.sp,fontWeight=FontWeight.Black);Text(l,color=Muted,fontSize=8.sp,fontWeight=FontWeight.Bold)}}
@Composable private fun MenuTile(icon:String,label:String,onClick:()->Unit,m:Modifier){Surface(m.clickable(onClick=onClick),color=Panel2,shape=RoundedCornerShape(22.dp),border=BorderStroke(1.dp,Color.White.copy(alpha=.08f))){Column(Modifier.padding(18.dp)){Text(icon,fontSize=28.sp);Text(label,color=Ink,fontWeight=FontWeight.Black)}}}

@Composable private fun CategoryScreen(hi:Boolean,back:()->Unit,start:(String)->Unit) {
    Column(Modifier.fillMaxSize()) { TopBar(if(hi)"विषय चुनें" else "Choose a topic",0,onBack=back);LazyColumn(contentPadding=PaddingValues(18.dp),verticalArrangement=Arrangement.spacedBy(9.dp)){items(categories){cat->Surface(Modifier.fillMaxWidth().clickable{start(cat)},color=Panel,shape=RoundedCornerShape(20.dp),border=BorderStroke(1.dp,Color.White.copy(alpha=.08f))){Row(Modifier.padding(16.dp),verticalAlignment=Alignment.CenterVertically){Text(categoryIcon(cat),fontSize=25.sp);Spacer(Modifier.size(13.dp));Column(Modifier.weight(1f)){Text(cat,color=Ink,fontWeight=FontWeight.Black);Text("${if(cat=="All")questions.size else questions.count{it.category==cat}} ${if(hi)"प्रश्न" else "questions"}",color=Muted,fontSize=11.sp)};Text("›",color=Sky,fontSize=25.sp)}}}} }
}
private fun categoryIcon(cat:String)=when(cat){"Gospels"->"✝";"Acts"->"🔥";"Kings"->"♛";"Prophets"->"📜";"Wisdom"->"💡";"Women"->"🌸";"Revelation"->"✨";else->"📖"}

@Composable private fun QuizScreen(hi:Boolean,title:String,list:List<Q>,sound:Boolean,tone:ToneGenerator,onAnswer:(Boolean)->Unit,onDone:()->Unit) {
    if(list.isEmpty()){LaunchedEffect(Unit){onDone()};return}
    var index by remember(list){mutableIntStateOf(0)}
    var selected by remember(list){mutableIntStateOf(-1)}
    var revealed by remember(list){mutableStateOf(false)}
    val haptic=LocalHapticFeedback.current
    val item=list[index]
    val opts=if(hi)item.hiOptions else item.enOptions
    Column(Modifier.fillMaxSize()) {
        TopBar(title,0)
        LinearProgressIndicator(progress={ (index+1f)/list.size },modifier=Modifier.fillMaxWidth().padding(horizontal=18.dp),color=Gold,trackColor=Panel2)
        Column(Modifier.padding(18.dp).fillMaxSize()) {
            Glass(Modifier.fillMaxWidth().weight(1f)) {
                Row { Surface(color=Sky.copy(alpha=.12f),shape=RoundedCornerShape(12.dp)){Text("${item.category} • ${item.difficulty}",Modifier.padding(8.dp),color=Sky,fontSize=10.sp,fontWeight=FontWeight.Black)};Spacer(Modifier.weight(1f));Text("${index+1}/${list.size}",color=Muted,fontWeight=FontWeight.Bold) }
                Spacer(Modifier.height(18.dp))
                Text(if(hi)item.hi else item.en,color=Ink,fontSize=23.sp,lineHeight=30.sp,fontWeight=FontWeight.Black)
                Spacer(Modifier.height(17.dp))
                opts.forEachIndexed { i,o ->
                    val correct=revealed&&i==item.correct
                    val wrong=revealed&&i==selected&&i!=item.correct
                    Surface(Modifier.fillMaxWidth().padding(vertical=5.dp).clickable(enabled=!revealed){selected=i;revealed=true;val ok=i==item.correct;onAnswer(ok);haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress);if(sound)tone.startTone(if(ok)ToneGenerator.TONE_PROP_ACK else ToneGenerator.TONE_PROP_NACK,90)},color=when{correct->Mint.copy(alpha=.16f);wrong->Bad.copy(alpha=.15f);else->Panel2},shape=RoundedCornerShape(18.dp),border=BorderStroke(1.dp,when{correct->Mint;wrong->Bad;else->Color.White.copy(alpha=.07f)})){
                        Row(Modifier.padding(15.dp)){Text("${('A'.code+i).toChar()}.",color=if(correct)Mint else Sky,fontWeight=FontWeight.Black);Spacer(Modifier.size(10.dp));Text(o,color=Ink,fontWeight=FontWeight.Bold)}
                    }
                }
                AnimatedVisibility(revealed) { Column { Spacer(Modifier.height(14.dp));Surface(color=Gold.copy(alpha=.09f),shape=RoundedCornerShape(18.dp),border=BorderStroke(1.dp,Gold.copy(alpha=.28f))){Column(Modifier.padding(14.dp)){Text(if(hi)"क्यों?" else "WHY?",color=Gold,fontSize=10.sp,fontWeight=FontWeight.Black);Text(if(hi)item.explainHi else item.explainEn,color=Ink);Spacer(Modifier.height(6.dp));Text("📖 ${if(hi)item.referenceHi else item.referenceEn}",color=Sky,fontWeight=FontWeight.Black,fontSize=12.sp)}};Spacer(Modifier.height(12.dp));HeroButton(if(index==list.lastIndex){if(hi)"परिणाम देखें" else "SEE RESULTS"}else{if(hi)"अगला प्रश्न →" else "NEXT QUESTION →"}){if(index==list.lastIndex)onDone()else{index++;selected=-1;revealed=false}} } }
            }
        }
    }
}

@Composable private fun ResultScreen(hi:Boolean,score:Int,total:Int,p:Player,home:()->Unit,again:()->Unit) {
    Column(Modifier.fillMaxSize()) { TopBar(if(hi)"क्विज़ पूर्ण" else "Quest Complete",p.coins);Column(Modifier.padding(20.dp),horizontalAlignment=Alignment.CenterHorizontally){Spacer(Modifier.height(30.dp));Box(Modifier.size(125.dp).clip(CircleShape).background(Brush.radialGradient(listOf(Gold,Sky))),contentAlignment=Alignment.Center){Text(if(score>=total*.8)"🏆" else "📖",fontSize=55.sp)};Spacer(Modifier.height(18.dp));Text("$score / $total",color=Ink,fontSize=42.sp,fontWeight=FontWeight.Black);Text(if(hi)"सही उत्तर" else "correct answers",color=Muted);Spacer(Modifier.height(18.dp));Glass(Modifier.fillMaxWidth()){Text(if(hi)"इनाम" else "REWARDS EARNED",color=Gold,fontSize=10.sp,fontWeight=FontWeight.Black);Text("+${score*10} XP    +${score*2} ✦",color=Ink,fontSize=22.sp,fontWeight=FontWeight.Black)};Spacer(Modifier.height(14.dp));HeroButton(if(hi)"↻ फिर से खेलें" else "↻ PLAY AGAIN",again);Spacer(Modifier.height(9.dp));OutlinedButton(onClick=home,modifier=Modifier.fillMaxWidth()){Text(if(hi)"होम" else "BACK HOME",color=Sky)}} }
}

@Composable private fun RewardScreen(hi:Boolean,p:Player,back:()->Unit) {
    Column(Modifier.fillMaxSize()) { TopBar(if(hi)"इनाम और उपलब्धियाँ" else "Rewards & Achievements",p.coins,onBack=back);LazyColumn(contentPadding=PaddingValues(18.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){item{Glass(Modifier.fillMaxWidth()){Text(if(hi)"आपका स्तर" else "YOUR LEVEL",color=Gold,fontSize=10.sp,fontWeight=FontWeight.Black);Text("${p.xp/100+1}",color=Ink,fontSize=40.sp,fontWeight=FontWeight.Black);LinearProgressIndicator(progress={(p.xp%100)/100f},modifier=Modifier.fillMaxWidth(),color=Sky,trackColor=Panel2)}};items(listOf("📖 First Steps" to 1,"🔥 Faithful Learner" to 7,"🏆 Bible Scholar" to 20,"✝ Gospel Explorer" to 30,"👑 Scripture Champion" to 50)){(name,need)->Glass(Modifier.fillMaxWidth()){Row{Text(name,color=Ink,fontWeight=FontWeight.Black,modifier=Modifier.weight(1f));Text(if(p.correct>=need)"✓" else "${p.correct}/$need",color=if(p.correct>=need)Mint else Muted)}}}} }
}

@Composable private fun SettingsScreen(hi:Boolean,sound:Boolean,music:Boolean,toggleLang:()->Unit,toggleSound:()->Unit,toggleMusic:()->Unit,back:()->Unit) {
    Column(Modifier.fillMaxSize()) { TopBar(if(hi)"सेटिंग्स" else "Settings",0,onBack=back);Column(Modifier.padding(18.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){Glass(Modifier.fillMaxWidth()){Row(verticalAlignment=Alignment.CenterVertically){Text(if(hi)"भाषा: हिन्दी" else "Language: English",color=Ink,fontWeight=FontWeight.Black,modifier=Modifier.weight(1f));Button(onClick=toggleLang){Text(if(hi)"EN" else "हिंदी")}}};Glass(Modifier.fillMaxWidth()){SettingRow(if(hi)"ध्वनि प्रभाव" else "Sound effects",sound,toggleSound);HorizontalDivider(color=Color.White.copy(alpha=.06f),modifier=Modifier.padding(vertical=9.dp));SettingRow(if(hi)"पृष्ठभूमि संगीत" else "Background music",music,toggleMusic)};Text(if(hi)"हर उत्तर के बाद सीखने योग्य व्याख्या और अध्याय/पद संदर्भ दिखाया जाता है।" else "Every answer includes a learning explanation and Bible chapter/verse reference.",color=Muted)} }
}
@Composable private fun SettingRow(label:String,value:Boolean,toggle:()->Unit){Row(verticalAlignment=Alignment.CenterVertically){Text(label,color=Ink,modifier=Modifier.weight(1f));Switch(checked=value,onCheckedChange={toggle()})}}
