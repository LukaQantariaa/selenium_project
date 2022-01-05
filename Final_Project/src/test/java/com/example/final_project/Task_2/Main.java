package com.example.final_project.Task_2;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.logevents.SelenideLogger;
import com.google.gson.JsonObject;
import io.qameta.allure.selenide.AllureSelenide;
import io.restassured.RestAssured;
import io.restassured.response.ResponseBody;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import java.util.List;
import static com.codeborne.selenide.Selenide.*;

public class Main {

    @BeforeClass
    public static void browserInit() {
        Configuration.browser = "edge";
        Configuration.startMaximized = true;
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @BeforeMethod
    public void init() {
        open("https://demoqa.com/books");
    }

    @Test
    public void Test() {
        $("#searchBox").setValue("O'Reilly Media");
        var domBooks = $$("#app > div > div > div.row > div.col-12.mt-4.col-md-6 > div.books-wrapper > div.ReactTable.-striped.-highlight > div.rt-table > div.rt-tbody  .rt-tr:not(.-padRow)").stream().toList();
        var apiBooks = getBooks();
        assert apiBooks != null;
        var filterBooks = apiBooks.stream().filter(book -> book.publisher.contains("O'Reilly Media")).toList();
        Assert.assertEquals(domBooks.size(), filterBooks.size());
        var last = checkIsLast(domBooks, apiBooks);
        Assert.assertNotEquals(last,true);
    }

    private List<BookDto> getBooks() {
        RestAssured.baseURI = "https://bookstore.toolsqa.com";
        RequestSpecification req = RestAssured.given();
        var data = new JsonObject();
        req.header("Content-Type", "application/json");
        req.body(data.toString());
        var res = ParseBody(req.get("/BookStore/v1/Books").getBody());
        if (res == null) {
            return null;
        }
        return res.books;
    }

    private BooksDto ParseBody(ResponseBody body) {
        try {
            return body.as(BooksDto.class);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    private boolean checkIsLast(List<SelenideElement> bookElements, List<BookDto> apiBooks) {
        var isLast = false;
        var lastbook = apiBooks.get(apiBooks.size() - 1);
        isLast = lastbook.title.contains("Understanding ECMAScript 6");
        if (!isLast) {
            return false;
        }
        isLast = bookElements.get(bookElements.size() - 1).getText().contains("Understanding ECMAScript 6");
        return isLast;
    }

}
