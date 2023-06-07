package com.starcloud.ops.llm.langchain.core;

import com.starcloud.ops.llm.langchain.LangChainConfiguration;
import com.starcloud.ops.llm.langchain.core.model.llm.document.SplitDetail;
import com.starcloud.ops.llm.langchain.core.model.llm.document.SplitRule;
import com.starcloud.ops.llm.langchain.core.indexes.parser.DocumentSegmentsParser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;

import java.util.List;

@SpringBootTest(classes = {LangChainConfiguration.class})
@RunWith(SpringRunner.class)
public class ParserTest {

    @Test
    public void splitTest() {
        String str = "【析评】\n" +
                "\n" +
                "此诗作于渊明三十九岁。敬远是渊明的堂弟，他们自幼关系亲密，成人后亦志趣相投，感情融洽。这一年敬远二十三岁，同渊明住在一起，并一道读书躬耕。这年春天，诗人开始到怀古田舍躬耕。一年的劳动，收成甚微。寒冷与贫乏，都预示着躬耕自资道路的极端艰辛。这首诗就是在年终腊月之时，渊明写给敬远，以寄托深刻的慨叹之情。\n" +
                "\n" +
                "寝息柴门，与世俗隔绝，荆扉常关，寒风袭来，穷困潦倒，固穷自守，历览千年古书，时常看见操守品德高尚的先贤烈士，自己愧然。这首诗是陶公归隐后第一年的纪实“录像”。\n" +
                "\n" +
                "在陶渊明面前有两条路：一是在官场里不断运作和升迁，那是阳关大道（“平津”）；另一条是退守田园，栖迟于衡门之下，这是独木小桥。陶渊明说，既然前一条路走不成，那么只好走后一条，这也不算是“拙”。话是这么说，却总是有点不得已而求其次的味道，有自我安慰的意思。\n" +
                "\n" +
                "事实上固守其穷决非易事。陶渊明在诗中坦率地说自己是“谬得固穷节”，论者或以为这是他的谦辞，其实这一句诗表明他本来并不想走这样一条路，现在只是不得已而为之罢了。陶渊明不是对于世事无所动心的人，但处在当时东晋统治阶级自相争夺严重的险恶环境中，他只能强作忘情，自求解脱。解脱之道，是守儒家的固穷之节，融道家的居高观世之情，但又不取儒家的迂腐，道家的泯没是非。\n" +
                "\n" +
                "不走坦途仕路，甘愿隐居于乡里，自力更生，躬耕自给，一年劳作，不得温饱，但仍不改志，岂为拙乎！真实地反映陶公当时的思想和志节。\n" +
                "\n" +
                "一向恬澹的诗人终于道出了“了无一可悦”的慨叹。\n" +
                "\n" +
                "这首诗绝大部分诗句意思都相当明确，只有结穴处“寄意一言外，兹契谁能别”两句颇有玄言的色彩。这里的“一言”，或谓指“固穷”，或谓指“栖迟讵为拙”，恐怕都不大合适，既然是“一言”，应当只能是指出上句之末的那个“拙”字——否则就不止“一言”了。\n" +
                "\n" +
                "“拙”字在陶诗中出现过多次。陶渊明后来往往在褒义上使用此字。在此诗中，“栖迟讵为拙”这一句是为“栖迟”亦即隐居辩护的，他说这样活着还不能说是“拙”，这里“拙”字明显是贬义的。当然，陶渊明立即又说，“拙”字在它的一般义之外还有言外之意，这就含有要替“拙”字推陈出新的意思了。诗中末句忽然发问道：谁能够对此作出分析研究呢？他大约是寄希望于他的从弟陶敬远罢，但也没有明言，此时诗人自己陷入了深沉的反思。前人论陶渊明此诗往往一味称道其高尚，而无视其情感上的矛盾纠葛，尚未可称为知言。\n" +
                "\n" +
                "此诗前半叙事、写景，后半议论，俱以情渗透其中。尽管事写得很简洁，景写得传神入化，议论很多；但终以情为主，而情偏没有直接表露。把悲愤沉痛和坚强，变成闲淡乐观和诙谐，把层波迭浪变为定流清水，陶诗的意境，自然达到了极顶的深厚和醇美。\n" +
                "\n" +
                "【辑评】\n" +
                "\n" +
                "黄文焕《陶诗析义》卷三：无一可悦，俯首自叹；时见遗烈，昂首自命。非所攀，又俯首自逊；苟不由，又昂首自尊。章法如层波叠浪。\n" +
                "\n" +
                "陈祚明评选《采菽堂古诗选》卷十三：“倾耳”二句写风雪得神，而高旷之怀，超脱如睹。……起四句，一句一意，一意一转，曲折尽致，全得子卿“骨肉缘枝叶”章法，而无揣摹之迹。\n" +
                "\n" +
                "延君寿《老生常谈》：“凄凄岁暮风……在目皓已洁。”自是咏雪名句。下接云“劲气侵襟袖，箪瓢谢屡设”。接得沉着有力量。又云“高操非所攀……栖迟讵为拙”，想见作者之磊落光明，傲物自高。每闻人称陶公恬淡，固也；然试想此等人物，如松柏之耐岁寒，其劲直之气与有生俱来，安能不偶然流露于楮墨之间\n";
        SplitRule splitRule = new SplitRule();
        splitRule.setAutomatic(true);
        List<SplitDetail> details = DocumentSegmentsParser.INSTANCE.splitText(str, splitRule);
        Assert.isTrue(details.size() > 0, "");
    }
}
