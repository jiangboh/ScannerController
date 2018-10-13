package com.bravo.xml;

import com.bravo.utils.Logs;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Created by admin on 2018-9-12.
 */

public class XmlCodec {

    static private final String TAG = "XmlCodec";

    static private String[] ErrorCode = {
            "XML解析/封装成功！",
            "收到的数据不是XML格式！",
            "XML中未获取到消息ID或消息类型！",
            "XML中未获取到任何参数！",
            "键值对列表为空！",
            "键值对列表中没有元整！",
            "键值对列表中元素格式不正确！",
            "创建Xml消息出错！",
    };

    static int index = 0;

    /// <summary>
    /// 通过错误编号返回错误描述
    /// </summary>
    /// <param name="errorno">错误编号</param>
    /// <returns></returns>
    static public String getErrorMsg(int errorno)
    {
        return ErrorCode[Math.abs(errorno)];
    }

    static private Node getCheildNode(Element xmlElement,String str)
    {
        NodeList nodes = xmlElement.getChildNodes();
        if (nodes == null) return null;

        for (int i =0 ;i < nodes.getLength();i++)
        {
            Node node = nodes.item(i);

            if (str.equals(node.getNodeName()))
                return node;
        }
        return null;
    }

    static private void addDicNodes(Document myXmlDoc, HashMap<String, Object> dic, Element levelType)
    {
        for (Map.Entry<String, Object> kvp : dic.entrySet())
        {
            if ( FindMsgStruct.AllNum.equalsIgnoreCase(String.valueOf(kvp.getValue()))) continue;

            String[] nameList = kvp.getKey().split("/");
            if (nameList.length < 1)
            {
                return;
            }
            Element levelParent = levelType;
            Boolean isFindNode = true;
            int length = nameList.length;
            for (int i = 0; i < length - 1; i++)  //添加子节点
            {
                if (isFindNode)
                {
                    Node selectNode = getCheildNode(levelParent, String.valueOf(nameList[i]));
                    if (null == selectNode)
                    {
                        isFindNode = false; //上层节点没找到，不用再找下层节点了。
                    }
                    else
                    {
                        levelParent = (Element)selectNode;
                        continue;
                    }
                }
                Element levelAdd = myXmlDoc.createElement(String.valueOf(nameList[i]));
                levelParent.appendChild(levelAdd);
                levelParent = levelAdd;
            }
            Element level = myXmlDoc.createElement(nameList[nameList.length - 1]);
            level.setTextContent(String.valueOf(kvp.getValue()));
            levelParent.appendChild(level);
        }
    }
    /// <summary>
    /// 将键值对封装成发给AP的xml消息
    /// </summary>
    /// <param name="KeyValueList">键值对列表</param>
    /// <returns>封装后的xml消息</returns>
    static public String EncodeApXmlMessage(Msg_Body_Struct TypeKeyValue)
    {
        //初始化一个xml实例
        Document myXmlDoc = null;

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();//DocumentBuilder 对象
            myXmlDoc = builder.newDocument();//包含整个xnl内容的 Document对象

            ////加入XML的声明段落,<?xml version="1.0" encoding="UTF-8"?>
            //Declaration rootElement = myXmlDoc.CreateXmlDeclaration("1.0", "UTF-8", null);

            //创建xml的根节点
            //myXmlDoc.appendChild(rootElement);

            //初始化第一层节点:message_content
            Element levelElement1 = myXmlDoc.createElement("message_content");
            myXmlDoc.appendChild(levelElement1);

            //添加消息id
            Element levelId = myXmlDoc.createElement("id");
            levelId.setTextContent(String.valueOf(TypeKeyValue.msgId));
            levelElement1.appendChild(levelId);

            //初始化第二层节点（消息类型）
            Element levelType = myXmlDoc.createElement(TypeKeyValue.type);
            levelElement1.appendChild(levelType);

            //添加属性字段
            addDicNodes(myXmlDoc, TypeKeyValue.dic, levelType);

            if (TypeKeyValue.n_dic != null)
            {
                int size = TypeKeyValue.n_dic.size();
                for (int i = 0;i < size;i++)
                {
                    Name_DIC_Struct x = TypeKeyValue.n_dic.get(i);
                    addDicNodes(myXmlDoc,x.dic, levelType);
                }
            }
            //将xml文件保存到指定的路径下
            //myXmlDoc.Save("d://data2.xml");

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();

            DOMSource domSource = new DOMSource(myXmlDoc);
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            ByteArrayOutputStream bos   =   new   ByteArrayOutputStream();
            transformer.transform(new DOMSource(myXmlDoc), new StreamResult(bos));

            return bos.toString();

           //return bos.toByteArray();
        }
        catch (Exception ex)
        {
            Logs.e(TAG,"封装XML出错。出错原因：" + ex.getMessage().toString(),true);
            return null;
        }
    }

    static private void GetAllKeyNodes (String keyName,Node node,HashMap<String, Object> KeyValueList)
    {
        String name;
        if (!noChildNodes(node))
        {
            NodeList nodeList =  node.getChildNodes();
            for (int i = 0 ;i< nodeList.getLength();i++)
            {
                Node n = nodeList.item(i);
                String nodeName = n.getNodeName().trim();
                if ( nodeName == "#text") {
                    continue;
                }

                if (keyName == null || keyName.isEmpty()) {
                    name = nodeName;
                } else {
                    name = String.format("%s/%s", keyName, nodeName);
                }
                GetAllKeyNodes(name,n,KeyValueList);
            }
        }
        else
        {
            if (KeyValueList.containsKey(keyName))
            {
                if (index == Integer.MAX_VALUE)
                {
                    index = 0;
                }
                index++;
                keyName = String.format("%s_#%d#", keyName, index);
            }
            KeyValueList.put(keyName, node.getTextContent().trim());
        }

        return ;
    }

    static private boolean noChildNodes(Node node)
    {
        NodeList list = node.getChildNodes();
        int len = list.getLength();
        for (int i =0 ;i<len;i++) {
            Node n = list.item(i);
            if (n.getNodeType() != Node.ELEMENT_NODE)
                continue;
            return false;
        }

        return true;
    }
    /// <summary>
    /// 解析收到的xml消息
    /// </summary>
    /// <param name="msg">xml消息</param>
    /// <returns>解析后的结构体</returns>
    static public Msg_Body_Struct DecodeApXmlMessage(String msg)
    {
        Node idNode = null;
        Node MsgTypeNode = null;

         Msg_Body_Struct TypeKeyValueList = new Msg_Body_Struct();
        InputStream inStream = new ByteArrayInputStream(msg.getBytes());
        DocumentBuilderFactory factory =  DocumentBuilderFactory.newInstance();
        Element root = null;
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();//DocumentBuilder 对象
            //BufferedReader br= new BufferedReader(new InputStreamReader(inStream,"utf-8"));
            BufferedReader br= new BufferedReader(new InputStreamReader(inStream,"UTF-8"));
            InputSource is = new InputSource(br);
            Document doc = builder.parse(is);//包含整个xnl内容的 Document对象
            root = doc.getDocumentElement();//得到 根节点
        } catch(Exception e){
            Logs.e(TAG,"加载Xml消息结构出错。",true);
            return null;
        }

        NodeList rootList = root.getChildNodes();//获取根节点下 所有的节点
        int len = rootList.getLength();
        for (int i =0 ;i<len;i++)
        {
            Node node = rootList.item(i);
            //去掉子节点
            if (node.getNodeType() != Node.ELEMENT_NODE)
                continue;

            if (node.getNodeName().equalsIgnoreCase("id"))
            {
                idNode = node;
            }
            else
            {
                MsgTypeNode = node;
            }
        }

        if (MsgTypeNode == null)
        {
            Logs.e(TAG,"在Xml消息中未找到消息类型节点。",true);
            return null;
        }

        if (idNode == null || idNode.getTextContent() == null || idNode.getTextContent().isEmpty())
            TypeKeyValueList.msgId = 0;
        else
            TypeKeyValueList.msgId = Integer.parseInt(idNode.getTextContent().trim(),10);

        TypeKeyValueList.type = MsgTypeNode.getNodeName();

        HashMap<String, Object> KeyValueList = new HashMap<String, Object>();
        GetAllKeyNodes(null, MsgTypeNode,KeyValueList);
        TypeKeyValueList.dic = KeyValueList;

        return TypeKeyValueList;
    }
}
