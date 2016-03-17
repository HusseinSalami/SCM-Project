package controllers;
import play.*;
import play.mvc.*;
import play.db.jpa.*;
import views.html.*;
import models.Person;
import models.Version;
import models.ChangeList;
import models.MetricTest;
import models.MetricDef;
import models.TestDef;
import models.TestRun;
import models.MetricValue;
import models.User;
import play.data.Form;
import java.util.List;
import java.util.*;
import java.util.ArrayList;
import static play.libs.Json.*;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Http;
import play.mvc.Http.Session;


public class Application extends Controller {
 
 public Result index() {
 return ok(index.render());
 }
 
 public Result getLoginPage()
 {
     return ok(signUp.render());
 }

 public Result getIndex2Page()
 {
     return ok(index2.render());
 }
 public Result getTestPage()
 {
     return ok(tester.render());
 }

 
 @Transactional(readOnly = true)
 public Result getAllChangeLists() {
 List<ChangeList> ListchangeList = (List<ChangeList>) JPA.em().createQuery("select p from ChangeList p order by p.versionId").getResultList();
 return ok(toJson(ListchangeList));
 }
 
 @Transactional
 public Result addVersion() {
     
 Session session = Http.Context.current().session();
 String email=session.get("username");
 Version version = Form.form(Version.class).bindFromRequest().get();
 JPA.em().persist(version);
 ChangeList change=new ChangeList();
 String id_version1;
 version= (Version) JPA.em().createQuery("SELECT p from Version p where p.name =:name").setParameter("name",version.name).getSingleResult();
 id_version1=version.id;
 change.setversionId(id_version1);
 change.description = "Base" + version.id;
 change.user=email;
 JPA.em().persist(change);
 change=(ChangeList) JPA.em().createQuery("SELECT p from ChangeList p WHERE p.versionId =:versionid").setParameter("versionid",id_version1).getSingleResult();
 String id_change=(String) change.id;
 Version version_base=(Version) JPA.em().find(Version.class,id_version1);
 version_base.setheadId(""+id_change);
 version_base.setbaseId(""+id_change);
 JPA.em().merge(version_base);
 return redirect(routes.Application.index());
 //return redirect(routes.Application.getTestPage());
     
 }
 
 
 @Transactional
 public Result addVersion3(String id,String versionName) {
Version version=new Version();
version.setheadId(id);
version.setbaseId(id);
version.name=(String)versionName;
JPA.em().persist(version);
return redirect(routes.Application.index());
 
 }
 @Transactional
    public Result createmetric() {
    MetricDef metric=new MetricDef();
    DynamicForm dynamicForm = Form.form().bindFromRequest();
    String name= dynamicForm.get("name");
    String description=dynamicForm.get("description");
    String tolerance=dynamicForm.get("tolerance");
    metric.name=name;
    metric.description=description;
    metric.tolerance=tolerance;
    JPA.em().persist(metric);
    return redirect(routes.Application.getIndex2());
 
 }
//ajouter les contraintes qui vont empecher a lutilisateur de creer des versions de meme nom, et des testdef aussi.

 @Transactional
    public Result createTest(String tags) {
        
    String[] array = tags.split(",");
        
    TestDef test=new TestDef();
    DynamicForm dynamicForm = Form.form().bindFromRequest();
    String name= dynamicForm.get("name");
    String description=dynamicForm.get("description");
    test.name=name;
    test.description=description;
    JPA.em().persist(test);
    
    TestDef test_modifier=(TestDef) JPA.em().createQuery("select DISTINCT p from TestDef p where p.name=:name1 and p.description=:name2").setParameter("name1",name).setParameter("name2",description).getSingleResult();
    
    
    String test_id=test_modifier.idTestDef;
    for(int i=0;i<array.length;++i)
    {
     MetricTest metric=new MetricTest();
     metric.idTestDef=test_id;
     metric.idMetricDef=array[i];
     JPA.em().persist(metric);
    }
    
    return redirect(routes.Application.getIndex2());
 
 }
 ///parler du workaroundd
 @Transactional
    public Result addMetricValue( )
    {
        
        DynamicForm dynamicForm = Form.form().bindFromRequest();

        List<List <MetricDef>> liste_metri=new ArrayList<>();
        List<TestRun> list_testRun=(List<TestRun>)JPA.em().createQuery("select distinct p from TestRun p, MetricValue v where p.idTestRun=v.idTestRun and v.value=:valeur").setParameter("valeur","null").getResultList();
        for(int i=0;i<list_testRun.size();++i)
            {   
                
                String test=list_testRun.get(i).idTestDef;
                
                List<MetricDef> testdef=(List<MetricDef>)JPA.em().createQuery("select distinct s from MetricTest p, TestDef t, MetricDef s where p.idMetricDef=s.idMetricDef AND p.idTestDef=t.idTestDef AND t.idTestDef=:value").setParameter("value",test).getResultList();
                
                liste_metri.add(i,testdef);
                
              for(int j=0;j<testdef.size();++j)
              {
                  String metric_id=testdef.get(j).idMetricDef;
                  String test_id= list_testRun.get(i).idTestRun;
                                
                  MetricValue metric_value=(MetricValue) JPA.em().createQuery("select p from MetricValue p where p.idTestRun=:test_id and p.idMetricDef=:metric_id").setParameter("test_id",test_id).setParameter("metric_id",metric_id).getSingleResult();
                  String value=dynamicForm.get(testdef.get(j).idMetricDef+test_id);
                  if(value.equals(""))
				  {
				  ;
				  }
				  else{
				  metric_value.value=value;
                  JPA.em().merge(metric_value);
				}
			  }
            }
            return redirect(routes.Application.getIndex2Page());
    }
 
    @Transactional(readOnly = true)
 public Result getTestDefOrder()
 {
      List<TestRun> list_testRun=(List<TestRun>)JPA.em().createQuery("select distinct p from TestRun p, MetricValue v where p.idTestRun=v.idTestRun and v.value=:valeur").setParameter("valeur","null").getResultList();
      List<TestDef> list_testDef=new ArrayList<>();
      for(int i=0;i<list_testRun.size();++i)
      {
         // String valeur=list_testRun.get(i).idTestDef;
         TestDef td=(TestDef)JPA.em().createQuery("select td from TestDef td where td.idTestDef=:value").setParameter("value",list_testRun.get(i).idTestDef).getSingleResult();
         
         list_testDef.add(i,td);  
      }
 return ok(toJson(list_testDef));
 }
 
 
 @Transactional(readOnly = true)
 public Result getMetricValue() {
 List<MetricValue> metricValue = (List<MetricValue>) JPA.em().createQuery("select p from MetricValue p order by p.idMetricValue").getResultList();
 return ok(toJson(metricValue));
 }
 
 
 @Transactional(readOnly = true)
 public Result getTestmetrique() {
 List<MetricTest> metricTest = (List<MetricTest>) JPA.em().createQuery("select p from MetricTest p").getResultList();
 return ok(toJson(metricTest));
 }
 
@Transactional(readOnly = true)
 public Result getTest() {
 List<TestDef> list_test = (List<TestDef>) JPA.em().createQuery("select p from TestDef p").getResultList();
 return ok(toJson(list_test));
 }


@Transactional(readOnly = true)
 public Result getTesteurNeedsTest()
{
  List<TestRun> list_testRun=(List<TestRun>)JPA.em().createQuery("select distinct p from TestRun p, MetricValue v where p.idTestRun=v.idTestRun and v.value=:valeur").setParameter("valeur","null").getResultList();
 
 return ok(toJson(list_testRun));
    
}

@Transactional(readOnly = true)
 public Result getTesteurNeedsMetric()
{
 List<List <MetricDef>> liste_metri=new ArrayList<>();
 List<TestRun> list_testRun=(List<TestRun>)JPA.em().createQuery("select distinct p from TestRun p, MetricValue v where p.idTestRun=v.idTestRun and v.value=:valeur").setParameter("valeur","null").getResultList();
 for(int i=0;i<list_testRun.size();++i)
 { 
     String test=list_testRun.get(i).idTestDef;
     List<MetricDef> testdef=(List<MetricDef>)JPA.em().createQuery("select distinct s from MetricTest p, TestDef t, MetricDef s where p.idMetricDef=s.idMetricDef AND p.idTestDef=t.idTestDef AND t.idTestDef=:value").setParameter("value",test).getResultList();
     liste_metri.add(i,testdef);
 }  
 return ok(toJson(liste_metri));  
}

//
 @Transactional(readOnly = true)
 public Result getTestRunCreate(String id) {

 List<TestDef> list_test = (List<TestDef>) JPA.em().createQuery("select p from TestDef p").getResultList();
 
 return ok(toJson(list_test));
 
 }

 @Transactional(readOnly = true)
 public Result getTesteurWork() {
 
 return ok(index2.render());
 
 }
 
  @Transactional
 public Result getTestRunDexideCreate(String id_test_def,String id_changeList)
 {
// TestRun t=new TestRun();
 String count=(String) JPA.em().createQuery("select count(x) from TestRun x where x.idTestDef=:name1 and x.idChangeList=:name2").setParameter("name1",id_test_def).setParameter("name2",id_changeList).getSingleResult().toString();
 if(count.equals("0"))
 {
 TestRun t=new TestRun();
 t.idTestDef=id_test_def;
 t.idChangeList=id_changeList;
 JPA.em().persist(t);
 String id_testRun=(String) JPA.em().createQuery("SELECT x.id FROM TestRun x WHERE x.idTestDef=:id1 and x.idChangeList=:id2").setParameter("id1",id_test_def).setParameter("id2",id_changeList).getSingleResult();
 
 
 List<MetricDef> testdef=(List<MetricDef>)JPA.em().createQuery("select s from MetricTest p, TestDef t, MetricDef s where p.idMetricDef=s.idMetricDef AND p.idTestDef=t.idTestDef AND t.idTestDef=:value").setParameter("value",id_test_def).getResultList();
 for(int i=0;i<testdef.size();++i)
 {
     MetricValue m=new MetricValue();
     m.idTestRun=id_testRun;
     m.idMetricDef=testdef.get(i).idMetricDef;
     m.value="null";
     JPA.em().persist(m);
 }
 }
 else
 {
     TestRun t= (TestRun) JPA.em().createQuery("select distinct t from TestRun t where t.idTestDef=:value1 and t.idChangeList=:value2").setParameter("value1",id_test_def).setParameter("value2",id_changeList).getSingleResult();
     String id_test_run = t.idTestRun;
     List<MetricValue> m_value= (List<MetricValue>) JPA.em().createQuery("select m from MetricValue m where m.idTestRun=:value").setParameter("value",id_test_run).getResultList();
     for(int i=0;i<m_value.size();++i)
    {
        m_value.get(i).value="null";
        JPA.em().merge(m_value.get(i));
    }
     //je met ;les valeurs du testRun a null
 }
 return redirect(routes.Application.index());
 }
 
 @Transactional(readOnly = true)
 public Result getTestRunAll()
 {
    List <TestRun> list_test_run=(List<TestRun>) JPA.em().createQuery("select p from TestRun p").getResultList();
    return ok(toJson(list_test_run));
 }
 
 @Transactional(readOnly = true)
 public Result getmetric() {
 List<MetricDef> metrics = (List<MetricDef>) JPA.em().createQuery("select p from MetricDef p").getResultList();
 return ok(toJson(metrics));
 }



 @Transactional(readOnly = true)
 public Result getVersions() {
 List<Version> versions = (List<Version>) JPA.em().createQuery("select p from Version p order by p.id").getResultList();
 return ok(toJson(versions));
 }
 
 @Transactional
 public Result addChangeList() {
 ChangeList changeList = Form.form(ChangeList.class).bindFromRequest().get();
 JPA.em().persist(changeList);
 return redirect(routes.Application.index());
 }
 
@Transactional
 public Result addChangeList2(String id,String description) {
 Session session = Http.Context.current().session();
 ChangeList changeList=new ChangeList();
 changeList.description=description;
 changeList.versionId=id;
 changeList.user=session.get("username");
 Version version=(Version) JPA.em().find(Version.class,id);
 JPA.em().persist(changeList);
 String change=(String) JPA.em().createQuery("SELECT MAX(c.id) FROM ChangeList c,Version v WHERE (c.versionId=v.id or c.id=v.baseId) and c.versionId=:id1").setParameter("id1",id).getSingleResult();
 version.setheadId(change);
 JPA.em().merge(version);
 return redirect(routes.Application.index());
 }
 
 @Transactional(readOnly = true)
 public Result getChangeLists() {
 List<ChangeList> changeLists = (List<ChangeList>) JPA.em().createQuery("select p from ChangeList p").getResultList();
 return ok(toJson(changeLists));
 }
 
  @Transactional(readOnly = true)
 public Result  getTestDef()
 {
     List<TestDef> liste_test_def=(List<TestDef>)JPA.em().createQuery("select p from TestDef p").getResultList();
     return ok(toJson(liste_test_def));
 }
//comparaison entre changeList

 @Transactional(readOnly = true)
 public Result  getComparePage(String value)
 {

List<Version> list_version_chercher=(List<Version>) JPA.em().createQuery("select distinct v from Version v, ChangeList c,TestRun tR,TestDef tD,MetricValue mV where (v.id=c.versionId or v.baseId=c.id) and c.id=tR.idChangeList and tR.idTestDef=tD.idTestDef and  mV.idTestRun=tR.idTestRun and mV.value!='null' and tD.name=:value1 order by v.id) ").setParameter("value1",value).getResultList();
List<Long> list_change=(List<Long>) JPA.em().createQuery("select count(distinct c) as ttt  from Version v, ChangeList c,TestRun tR,TestDef tD,MetricValue mV where (v.id=c.versionId or v.baseId=c.id) and c.id=tR.idChangeList and tR.idTestDef=tD.idTestDef and  mV.idTestRun=tR.idTestRun and mV.value!='null' and tD.name=:value1 group by v.id ) ").setParameter("value1",value).getResultList();

List<Version>list_version=new ArrayList<>();
for(int k=0;k<(list_version_chercher).size();++k)
{

    Long a=(Long)list_change.get(k);
    if(a>= 2)   
    list_version.add(list_version_chercher.get(k));
    
}

List<List<ChangeList>> list_cL= new ArrayList<>();
for(int i=0;i<list_version.size();++i)
{
List<ChangeList> list=(List<ChangeList>) JPA.em().createQuery("select c from ChangeList c where c.id in(select p.id from ChangeList p where p.versionId=:value1) or c.id in(select v.baseId from Version v where v.id=:value2) ").setParameter("value1",list_version.get(i).id).setParameter("value2",list_version.get(i).id).getResultList();

list_cL.add(i,list);
    
}
return ok(comparaisonPage.render(list_version,list_cL,value));
 }
 
 
  @Transactional(readOnly = true)
 public Result getChangeListVersionTestDef(String nameTestDef,String version)
 {
    List<ChangeList> list=(List<ChangeList>) JPA.em().createQuery("select distinct c from ChangeList c,TestRun tR,TestDef tD where c.id= tR.idChangeList and tR.idTestDef=tD.idTestDef and tD.name =:value2 and (c.id in(select p.id from ChangeList p where p.versionId=:value1) or c.id in(select v.baseId from Version v where v.id=:value1)) ").setParameter("value1",version).setParameter("value2",nameTestDef).getResultList();
     
    return ok(toJson(list));
 }
 
 @Transactional(readOnly=true)
 public Result getMetricValueTest(String reference, String testdef)
 {
 List<MetricValue>mv_reference=(List<MetricValue>)JPA.em().createQuery("select distinct m from MetricValue m where m.idTestRun in (select tR.idTestRun from TestRun tR,ChangeList cL where tR.idChangeList=cL.id and cL.id=:value1) and m.idMetricDef in (select mD.idMetricDef from MetricDef mD,MetricTest mT where mD.idMetricDef=mT.idMetricDef and mT.idTestDef in(select t.idTestDef from TestDef t where t.name=:value2))").setParameter("value1",reference).setParameter("value2",testdef).getResultList();
     return ok(toJson(mv_reference));
 }
 
 @Transactional(readOnly = true)
 public Result getComparerChangeList(String testdef,String reference,String tester)
 {
 List<MetricValue>mv_reference=(List<MetricValue>)JPA.em().createQuery("select distinct m from MetricValue m where m.idTestRun in (select tR.idTestRun from TestRun tR,ChangeList cL where tR.idChangeList=cL.id and cL.id=:value1) and m.idMetricDef in (select mD.idMetricDef from MetricDef mD,MetricTest mT where mD.idMetricDef=mT.idMetricDef and mT.idTestDef in(select t.idTestDef from TestDef t where t.name=:value2) )").setParameter("value1",reference).setParameter("value2",testdef).getResultList();

 List<MetricValue>mv_tester=(List<MetricValue>)JPA.em().createQuery("select distinct m from MetricValue m where m.idTestRun in (select tR.idTestRun from TestRun tR,ChangeList cL where tR.idChangeList=cL.id and cL.id=:value1) and m.idMetricDef in (select mD.idMetricDef from MetricDef mD,MetricTest mT where mD.idMetricDef=mT.idMetricDef and mT.idTestDef in(select t.idTestDef from TestDef t where t.name=:value2))").setParameter("value1",tester).setParameter("value2",testdef).getResultList();

String resultat="neutre";
 for(int i=0;i<mv_tester.size();++i)
 {
     String metricdefid=mv_reference.get(i).idMetricDef;
     for(int j=0;j<mv_reference.size();++j)
     {
         if((mv_reference.get(j).idMetricDef).equals(metricdefid))
         {
             double tolerancePourcentage =Double.parseDouble((String)JPA.em().createQuery("select mD.tolerance from MetricDef mD where mD.idMetricDef=:value").setParameter("value",metricdefid).getSingleResult());
             double tolerance_metrique=(tolerancePourcentage)/100;
             double tolerance=tolerance_metrique*(Double.parseDouble((mv_reference.get(j).value)));
             double value_reference=Double.parseDouble(mv_reference.get(j).value);
             double value_tester=Double.parseDouble(mv_tester.get(i).value);
             if(value_reference>value_tester)
             {
                 if(value_reference>(value_tester+tolerance))
                     resultat="avance";
                 else
                 {
                     if(resultat.equals("avance"))
                      ;
                      else
                     resultat="neutre";
                     
                 }
                 
             }
             
             else
             {
                if(value_reference==value_tester)
                    {
                        if(resultat.equals("avance"))
                        ;
                        else
                       resultat="neutre";
                        
                    }
                else
                {
                    if((value_reference+tolerance)<value_tester)
                    {
                     resultat="retrait";
                     return ok(toJson(resultat));
                    }
                    
                    else
                    {
                        if(resultat.equals("avance"))
                        ;
                        
                        else
                         resultat="neutre";
                        
                        
                    }
                    
               }
               
               
             }


             
         }
         
     }
 }
    return ok(toJson(resultat));
 }
 
 
 @Transactional(readOnly = true)
 public Result getValidateVersion(String testName,String versionId)
{
    String id_head=(String) JPA.em().createQuery("select v.headId from Version v where v.id=:value1").setParameter("value1",versionId).getSingleResult();
    
    String id_base=(String) JPA.em().createQuery("select v.baseId from Version v where v.id=:value1").setParameter("value1",versionId).getSingleResult();
    
    Long head=(Long) JPA.em().createQuery("select count(tR) from TestRun tR, TestDef tD where tR.idChangeList=:value1 and tR.idTestDef=tD.idTestDef and tD.name=:value2").setParameter("value1",id_head).setParameter("value2",testName).getSingleResult();
    
    Long base=(Long) JPA.em().createQuery("select count(tR) from TestRun tR, TestDef tD where tR.idChangeList=:value1 and tR.idTestDef=tD.idTestDef and tD.name=:value2").setParameter("value1",id_base).setParameter("value2",testName).getSingleResult();
    
    if(head!=0 && base!=0)
    {
        return ok(toJson("true"));
        
    }
    else
    return ok(toJson("false"));
}
    
 @Transactional(readOnly = true)
 public Result validateVersion(String testName,String versionId)
 {
    String id_head=(String) JPA.em().createQuery("select v.headId from Version v where v.id=:value1").setParameter("value1",versionId).getSingleResult();
    String id_base=(String) JPA.em().createQuery("select v.baseId from Version v where v.id=:value1").setParameter("value1",versionId).getSingleResult();
    
 
   return  getComparerChangeList(testName,id_base,id_head);
 }
 //ajouter
 @Transactional(readOnly = true)
 public Result  getMetricDefNom(String nameTestDef)
 {
     List<MetricDef> list_metric=(List<MetricDef>)JPA.em().createQuery("select distinct mD from MetricDef mD,MetricTest mT,TestDef tD where mD.idMetricDef=mT.idMetricDef and mT.idTestDef=tD.idTestDef and tD.name=:value").setParameter("value",nameTestDef).getResultList();
    return ok(toJson(list_metric));     
 }
     
@Transactional(readOnly = true)
 public Result getMetricValuesTemplateValidate(String testDef, String versionId)
 {
    String id_head=(String) JPA.em().createQuery("select v.headId from Version v where v.id=:value1").setParameter("value1",versionId).getSingleResult();
    String id_base=(String) JPA.em().createQuery("select v.baseId from Version v where v.id=:value1").setParameter("value1",versionId).getSingleResult();
 
    List<MetricValue> listMetricValueBase=(List<MetricValue>)JPA.em().createQuery("select distinct mV from MetricValue mV,TestRun tR, TestDef tD,ChangeList cL,MetricTest mT,MetricDef mD  where tD.name=:value1 and mT.idMetricDef=mD.idMetricDef and mT.idTestDef=tD.idTestDef and cL.id=tR.idChangeList  and mV.idTestRun=tR.idTestRun and cL.id=:value2 order by mV.idMetricValue").setParameter("value1",testDef).setParameter("value2",id_base).getResultList();     
    List<MetricValue> listMetricValueHead=(List<MetricValue>)JPA.em().createQuery("select distinct mV from MetricValue mV,TestRun tR, TestDef tD,ChangeList cL,MetricTest mT,MetricDef mD  where tD.name=:value1 and mT.idMetricDef=mD.idMetricDef and mT.idTestDef=tD.idTestDef and cL.id=tR.idChangeList  and mV.idTestRun=tR.idTestRun and cL.id=:value2 order by mV.idMetricValue").setParameter("value1",testDef).setParameter("value2",id_head).getResultList();     
  
  
    
    List<List<MetricValue>> list_metric=new ArrayList<>();
  
    list_metric.add(listMetricValueBase);
  
    list_metric.add(listMetricValueHead);
  
    return ok(toJson(list_metric)); 
 }
 
 
 @Transactional(readOnly = true)
 public Result getHeadBaseId(String versionId)
 {
     List<String> list=(List<String>)JPA.em().createQuery("select cL.id from ChangeList cL,Version v where (cL.id=v.headId or cL.id=v.baseId) and v.id=:value order by cL.id").setParameter("value",versionId).getResultList();

    return ok(toJson(list));     
 }
 
 
 @Transactional(readOnly = true)
 public Result getMetricValuesTemplateCompare(String nameTestDef,String reference,String comp)
 {
    List<MetricValue> listMetricValueReference=(List<MetricValue>)JPA.em().createQuery("select distinct mV from MetricValue mV,TestRun tR, TestDef tD,ChangeList cL,MetricTest mT,MetricDef mD  where tD.name=:value1 and mT.idMetricDef=mD.idMetricDef and mT.idTestDef=tD.idTestDef and cL.id=tR.idChangeList  and mV.idTestRun=tR.idTestRun and cL.id=:value2 order by mV.idMetricValue").setParameter("value1",nameTestDef).setParameter("value2",reference).getResultList();     
    List<MetricValue> listMetricValueHead=(List<MetricValue>)JPA.em().createQuery("select distinct mV from MetricValue mV,TestRun tR, TestDef tD,ChangeList cL,MetricTest mT,MetricDef mD  where tD.name=:value1 and mT.idMetricDef=mD.idMetricDef and mT.idTestDef=tD.idTestDef and cL.id=tR.idChangeList  and mV.idTestRun=tR.idTestRun and cL.id=:value2 order by mV.idMetricValue").setParameter("value1",nameTestDef).setParameter("value2",comp).getResultList();     
     
    List<List<MetricValue>> list_metric=new ArrayList<>();
    
    list_metric.add(listMetricValueReference);
    list_metric.add(listMetricValueHead);
    
     return ok(toJson(list_metric));
 }
 
 @Transactional(readOnly = true)
 public Result getmetricValueTemplateCompare(String metricChoixId,String reference,String comp)
 {
     MetricValue metricValueReference=(MetricValue)JPA.em().createQuery("select distinct mV from MetricValue mV,TestRun tR, ChangeList cL  where mV.idMetricDef=:value1 and cL.id=tR.idChangeList  and mV.idTestRun=tR.idTestRun and cL.id=:value2").setParameter("value1",metricChoixId).setParameter("value2",reference).getSingleResult();
     
     MetricValue metricValueHead=(MetricValue)JPA.em().createQuery("select distinct mV from MetricValue mV,TestRun tR, ChangeList cL  where mV.idMetricDef=:value1 and cL.id=tR.idChangeList  and mV.idTestRun=tR.idTestRun and cL.id=:value2").setParameter("value1",metricChoixId).setParameter("value2",comp).getSingleResult();
 
    List<MetricValue> list_metric=new ArrayList<>();
    list_metric.add(metricValueReference);
    list_metric.add(metricValueHead);
 
    return ok(toJson(list_metric));
     
 }
 
  @Transactional(readOnly = true)
 public Result getComparerChangeListUnique(String metricId,String reference,String tester)
 {
    MetricValue mv_reference=(MetricValue) JPA.em().createQuery("select distinct mV from MetricValue mV,TestRun tR,ChangeList cL  where mV.idMetricDef=:value1 and cL.id=tR.idChangeList and mV.idTestRun=tR.idTestRun and cL.id=:value2").setParameter("value1",metricId).setParameter("value2",reference).getSingleResult();
    
    MetricValue mv_tester=(MetricValue) JPA.em().createQuery("select distinct mV from MetricValue mV,TestRun tR, ChangeList cL  where mV.idMetricDef=:value1 and cL.id=tR.idChangeList  and mV.idTestRun=tR.idTestRun and cL.id=:value2").setParameter("value1",metricId).setParameter("value2",tester).getSingleResult();


              String resultat="neutre";
 
             double tolerancePourcentage =Double.parseDouble((String)JPA.em().createQuery("select mD.tolerance from MetricDef mD where mD.idMetricDef=:value").setParameter("value",metricId).getSingleResult());
             double tolerance_metrique=(tolerancePourcentage)/100;
             double tolerance=tolerance_metrique*(Double.parseDouble((mv_reference.value)));
             double value_reference=Double.parseDouble(mv_reference.value);
             double value_tester=Double.parseDouble(mv_tester.value);
             if(value_reference>value_tester)
             {
                 if(value_reference>(value_tester+tolerance))
                     resultat="avance";
                 else
                 {
                     if(resultat.equals("avance"))
                      ;
                      else
                     resultat="neutre";
                     
                 }
                 
             }
             
             else
             {
                if(value_reference==value_tester)
                    {
                        if(resultat.equals("avance"))
                        ;
                        else
                       resultat="neutre";
                        
                    }
                else
                {
                    if((value_reference+tolerance)<value_tester)
                    {
                     resultat="retrait";
                     return ok(toJson(resultat));
                    }
                    
                    else
                    {
                        if(resultat.equals("avance"))
                        ;
                        
                        else
                         resultat="neutre";
                    }
                    
               }
             
             }   
    return ok(toJson(resultat));
 }
 
 public Result getIndex2()
 {
     return  ok(index2.render());
 }
 ////////////////////////////Register Part
 
 
  @Transactional
public Result signUp(String type)
{
    DynamicForm dynamicForm = Form.form().bindFromRequest();
    String email= dynamicForm.get("email");
    String password=dynamicForm.get("password");
    Long count=(Long)JPA.em().createQuery("select count(u) from User u where u.email=:value").setParameter("value",email).getSingleResult();
 
 if(count!=0)
 {
         return redirect(routes.Application.getSignUp());
 }
 else 
 {
   User user = new User();
   user.setEmail(email);
   user.setPassword(password);
   if(type.equals("Testeur"))
   user.setisAdmin(true);
  
  if(type.equals("Developpeur"))
   user.setisAdmin(false);
  
   JPA.em().persist(user);
   session().clear();
   session("username",email);
   if(user.isAdmin==false)
      {
        return redirect(routes.Application.index());
      }
   
  else
    {
        return redirect(routes.Application.index2()); 
    }
     
 }
} 

  @Transactional(readOnly = true)
 public Result accueil()
 {
     return ok(accueil.render());
 }
 
  @Transactional(readOnly = true)
public Result getSignUp()
{
    return ok(signUp.render());
}

 @Transactional
public  Result login() {
    
  DynamicForm dynamicForm = Form.form().bindFromRequest();
  String email= dynamicForm.get("email");
  String password=dynamicForm.get("password");
  byte[] a=User.getSha512(password);

  Long count=(Long)JPA.em().createQuery("select count(u) from User u where u.email=:value1 ").setParameter("value1",email).getSingleResult();
 if (count==0)
 {
  // return ok(signUp.render("user does not exist")); 
 //return ok(signUp.render()); 
 return redirect(routes.Application.getLoginPage());
 }
 else{
   User user=(User) JPA.em().createQuery("select u from User u where u.email=:value1").setParameter("value1",email).getSingleResult();
   
   if(Arrays.equals(a,user.shaPassword))
 {
   
   session().clear();
   session("username", email);
   
   if(user.isAdmin==true)
      // return ok(index2.render());
   return redirect(routes.Application.getIndex2Page());
   
   else
    //   return ok(index.render());
   return redirect(routes.Application.index());
 }
 else
 {
      //  return ok(signUp.render("wrong password"));    
 
     // return ok(signUp.render());   
     return redirect(routes.Application.getLoginPage());
 }
 
 }  
}


@Transactional(readOnly = true)
 public Result getUsers()
 {
     List<User> s=(List<User>)JPA.em().createQuery("select u from User u ").getResultList();
 
     return ok(toJson(s));
 }
 
@Transactional(readOnly = true)
 public  Result logout() {
 session().clear();
// session().destroy();
// return ok(signUp.render("signed Out"));

 return ok(signUp.render());
     
 }
 
 @Transactional(readOnly = true)
 public Result getCouleurMetriqueValidate(String testName,String versionId)
{
    String id_head=(String) JPA.em().createQuery("select v.headId from Version v where v.id=:value1").setParameter("value1",versionId).getSingleResult();
    String id_base=(String) JPA.em().createQuery("select v.baseId from Version v where v.id=:value1").setParameter("value1",versionId).getSingleResult();
    return  getCouleurMetriqueComparer(testName,id_base,id_head);
    
    /*
    String base=(String)JPA.em().createQuery("select v.baseId from Version v where v.id=:value").setParameter("value",versionId).getSingleResult();
    String head=(String)JPA.em().createQuery("select v.headId from Version v where v.id=:value").setParameter("value",versionId).getSingleResult();
    
   // List<MetricValue>
    
    String metricValeurBase=(String) JPA.em().createQuery("select distinct m.value from MetricValue m ,TestRun tR, MetricDef mD,TestDef tD where tR.idChangeList=:value3 and m.idTestRun=tR.idTestRun and m.idMetricDef=mD.idMetricDef and mD.name=:value1 and tD.idTestDef=tR.idTestDef and tD.name=:value2").setParameter("value1",metricDefName).setParameter("value2",testDefName).setParameter("value3",base).getSingleResult();
    String metricValeurHead=(String) JPA.em().createQuery("select distinct m.value from MetricValue m ,TestRun tR, MetricDef mD,TestDef tD where tR.idChangeList=:value3 and m.idTestRun=tR.idTestRun and m.idMetricDef=mD.idMetricDef and mD.name=:value1 and tD.idTestDef=tR.idTestDef and tD.name=:value2").setParameter("value1",metricDefName).setParameter("value2",testDefName).setParameter("value3",head).getSingleResult();

    String tolerance =(String) JPA.em().createQuery("select m.tolerance from MetricDef m where m.name=:value").setParameter("value",metricDefName).getSingleResult();
    String resultat="neutre";
    Double toleranceChiffre=(Double.parseDouble(tolerance))/100;
    Double toleranceUtilise=toleranceChiffre*(Double.parseDouble((metricValeurBase)));
    Double value_reference=Double.parseDouble(metricValeurBase);
    Double value_tester=Double.parseDouble(metricValeurHead);
    if(value_reference>value_tester)
             {
                 if(value_reference>(value_tester+toleranceUtilise))
                     resultat="avance";
                 else
                 {
                     if(resultat.equals("avance"))
                      ;
                      else
                     resultat="neutre";
                     
                 }
                 
             }
             
             else
             {
                if(value_reference==value_tester)
                    {
                        if(resultat.equals("avance"))
                        ;
                        else
                       resultat="neutre";
                        
                    }
                else
                {
                    if((value_reference+toleranceUtilise)<value_tester)
                    {
                     resultat="retrait";
                     return ok(toJson(resultat));
                    }
                    
                    else
                    {
                        if(resultat.equals("avance"))
                        ;
                        
                        else
                         resultat="neutre";
                    }
                    
               }
             
             }
             return ok(toJson(resultat));
    
    */
} 

@Transactional(readOnly = true)
 public Result getCouleurMetriqueComparer(String testdef,String reference,String tester)
{
    List<String>couleur=new ArrayList<>();
    List<MetricValue>mv_reference=(List<MetricValue>)JPA.em().createQuery("select distinct m from MetricValue m where m.idTestRun in (select tR.idTestRun from TestRun tR,ChangeList cL where tR.idChangeList=cL.id and cL.id=:value1) and m.idMetricDef in (select mD.idMetricDef from MetricDef mD,MetricTest mT where mD.idMetricDef=mT.idMetricDef and mT.idTestDef in(select t.idTestDef from TestDef t where t.name=:value2)) order by m.idMetricValue").setParameter("value1",reference).setParameter("value2",testdef).getResultList();

 List<MetricValue>mv_tester=(List<MetricValue>)JPA.em().createQuery("select distinct m from MetricValue m where m.idTestRun in (select tR.idTestRun from TestRun tR,ChangeList cL where tR.idChangeList=cL.id and cL.id=:value1) and m.idMetricDef in (select mD.idMetricDef from MetricDef mD,MetricTest mT where mD.idMetricDef=mT.idMetricDef and mT.idTestDef in(select t.idTestDef from TestDef t where t.name=:value2)) order by m.idMetricValue").setParameter("value1",tester).setParameter("value2",testdef).getResultList();

String resultat="neutre";
 for(int i=0;i<mv_tester.size();++i)
 {
     String metricdefid=mv_reference.get(i).idMetricDef;
     for(int j=0;j<mv_reference.size();++j)
     {
         if((mv_reference.get(j).idMetricDef).equals(metricdefid))
         {
             double tolerancePourcentage =Double.parseDouble((String)JPA.em().createQuery("select mD.tolerance from MetricDef mD where mD.idMetricDef=:value").setParameter("value",metricdefid).getSingleResult());
             double tolerance_metrique=(tolerancePourcentage)/100;
             double tolerance=tolerance_metrique*(Double.parseDouble((mv_reference.get(j).value)));
             double value_reference=Double.parseDouble(mv_reference.get(j).value);
             double value_tester=Double.parseDouble(mv_tester.get(i).value);
             if(value_reference>value_tester)
             {
                 if(value_reference>(value_tester+tolerance))
                     //resultat="avance";
                     couleur.add(i,"avance");
                 else
                 {
                  couleur.add(i,"neutre");   
                 }
                 
             }
             
             else
             {
                if(value_reference==value_tester)
                    {
                        couleur.add(i,"neutre");
                        
                    }
                else
                {
                    if((value_reference+tolerance)<value_tester)
                    {
                     //resultat="retrait";
                    // return ok(toJson(resultat));
                    couleur.add(i,"retrait");
                    }
                    
                    else
                    {
                        couleur.add(i,"neutre");
                        
                    }
                    
               }
               
               
             }


             
         }
         
     }
 }
    return ok(toJson(couleur));
   
} 
 
 
 public Result index2() {
 return ok(index2.render());
 }
 
 @Transactional(readOnly = true)
public Result getNomVersionCorrespondant(String changeList)
{
String nom= (String) JPA.em().createQuery("select distinct v.name from Version v,ChangeList cL where cL.id=:value and cL.versionId=v.id").setParameter("value",changeList).getSingleResult();
return ok(toJson(nom));    
}
 
 @Transactional(readOnly = true)
public Result getChangeListById(String versionId)
{
    List<ChangeList> list_ChangeList=(List<ChangeList>)JPA.em().createQuery("select distinct cL from ChangeList cL where cL.id in (select  c.id from ChangeList c where c.versionId=:value) or cL.id in (select b.baseId from Version b where b.id=:value) order by cL.id").setParameter("value",versionId).getResultList();
    return ok(toJson(list_ChangeList));
}

@Transactional
public  Result deleteAllRows()
{
    JPA.em().createQuery("DELETE FROM ChangeList").executeUpdate();
    JPA.em().createQuery("DELETE FROM MetricDef").executeUpdate();
    JPA.em().createQuery("DELETE FROM MetricTest").executeUpdate();
    
    JPA.em().createQuery("DELETE FROM MetricValue").executeUpdate();
    JPA.em().createQuery("DELETE FROM TestDef").executeUpdate();
    
    JPA.em().createQuery("DELETE FROM TestRun").executeUpdate();
    JPA.em().createQuery("DELETE FROM User").executeUpdate();
    JPA.em().createQuery("DELETE FROM Version").executeUpdate();
    
    return redirect(routes.Application.getLoginPage());
}
    
}

 

