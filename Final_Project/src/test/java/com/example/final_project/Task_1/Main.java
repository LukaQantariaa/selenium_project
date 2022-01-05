package com.example.final_project.Task_1;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.logevents.SelenideLogger;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.JsonObject;
import io.qameta.allure.selenide.AllureSelenide;
import io.restassured.RestAssured;
import io.restassured.response.ResponseBody;
import io.restassured.specification.RequestSpecification;
import org.codehaus.jackson.map.ObjectMapper;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.devtools.v85.network.model.Request;
import org.testng.Assert;
import org.testng.annotations.*;

import static com.codeborne.selenide.Selenide.*;

public class Main {

    @DataProvider(name = "users")
    public Object[][] dpMethod() {
        return new Object[][]{
                {"Luka!@#123", "Luka!@#123"},
        };
    }

    @BeforeClass
    public static void browserInit() {
        Configuration.browser = "chrome";
        Configuration.startMaximized = true;
        SelenideLogger.addListener("allure", new AllureSelenide());
        open("https://demoqa.com/login");
    }

//    @BeforeMethod
//    public void init() {
//
//    }

    @Test(dataProvider = "users", priority = 0)
    public void RegisterUser(String userName, String password) {
        RestAssured.baseURI = "https://bookstore.toolsqa.com";

        RequestSpecification req = RestAssured.given();

        var bodyData = new JsonObject();
        bodyData.addProperty("userName", userName);
        bodyData.addProperty("password", password);

        var jsonString = bodyData.toString();
        req.header("Content-Type", "application/json");
        req.body(jsonString);


        var res = req.post("/Account/v1/User");
        var statusCode = res.getStatusCode();


        Assert.assertEquals(statusCode, 200 | 201);

    }

    @Test(dataProvider = "users", priority = 1)
    public void LogIn(String userName, String password) throws InterruptedException {
        $("#userName").setValue(userName);
        $("#password").setValue(password);
        $("#login").click();

        Thread.sleep(200);
        var deleteAccountSelectorString =
                $x("/html/body/div[2]/div/div/div[2]/div[2]/div[1]/div[3]/div[2]/button");
        $(deleteAccountSelectorString).scrollIntoView(true);
        deleteAccountSelectorString.click();

        $("#closeSmallModal-ok").click();
        var txt = switchTo().alert().getText();
        switchTo().alert().accept();
        Assert.assertEquals(txt, "User Deleted.");
        Thread.sleep(100);
    }


    @Test(dataProvider = "users", priority = 1)
    public void messageValidation(String userName, String password) throws InterruptedException {
        $("#userName").setValue(userName);
        $("#password").setValue(password);
        $("#login").click();
        System.out.println($x("/html/body/div[2]/div/div/div[2]/div[2]/div[1]/form/div[5]/div/p").getText());
        Assert.assertEquals($("#name").getText(), "Invalid username or password!");
    }

}
