package cn.zhouchenxi.app.student.generator;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

/**
 * Created by JW on 2015/11/5 09:44.
 * Email : 1481013719@qq.com
 * Description :
 */
public class ExampleDaoGenerator {

    public static void main(String[] args) {

        try {
            /**
             * 参数1：数据库版本号
             * 参数2：默认的java package,如果不修改默认的报名，生成的dao和model都会在该包下
             */
            Schema schema = new Schema(1, "cn.zhouchenxi.app.student.dbmodel");
            // 修改dao的包路径，dao将会生成在这个包下
            schema.setDefaultJavaPackageDao("cn.zhouchenxi.app.student.dao");

            // 模式（Schema）同时也拥有两个默认的 flags，分别用来标示 entity 是否是 activie 以及是否使用 keep sections。
            // schema2.enableActiveEntitiesByDefault();
            // schema2.enableKeepSectionsByDefault();

            addEntity(schema); // 添加一个实体对象

            // 自动生成代码，第二个参数：指定输出路径
            new DaoGenerator().generateAll(schema, "/Users/xixi/AndroidStudioProjects/student/app/src/main/java");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 添加实体
     *
     * @param schema
     */
    private static void addEntity(Schema schema) {

        /**
         * 一个实体类就关联到数据库中的一张表，数据库中表的名字默认就是类的名字
         */
        Entity task = schema.addEntity("Task");

        //task.setTableName("task"); // 可以设置表的名字

        // GreenDao会自动根据实体类的属性值来创建表的字段，并赋予默认值
       // task.addIdProperty().primaryKey().autoincrement();  // 指定id，主键自增

        task.addIdProperty().primaryKey();
        task.addIntProperty("fromuid").notNull();
        task.addStringProperty("title").notNull();
        task.addStringProperty("detail").notNull();
        task.addStringProperty("author").notNull();
        task.addStringProperty("time").notNull();
        task.addStringProperty("content");


    }

}