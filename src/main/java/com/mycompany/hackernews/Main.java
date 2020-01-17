package com.mycompany.hackernews;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import org.w3c.dom.DOMException;

public class Main {

	@Parameter(names = "--posts", validateWith = InputValidation.class)
	private int posts;

	public static void main(String... argv) {
		Main main = new Main();
		JCommander.newBuilder().addObject(main).build().parse(argv);
		main.run();
	}

	public void run() {
		WebScraper(posts);
	}

	public void WebScraper(int input) {

		String baseUrl = "https://news.ycombinator.com/";
		int pagination = 1;
		WebClient client = new WebClient();
		client.getOptions().setCssEnabled(false);
		client.getOptions().setJavaScriptEnabled(false);
		try {

			HtmlPage page = client.getPage(baseUrl);
			List<HtmlElement> items = (List<HtmlElement>) page.getByXPath("//tr[contains(@class, 'athing')]");

			for (int i = 0; i < posts; i++) {

				if (i >= 30) {
					i = 0;
					posts -= 30;
					pagination++;
					page = client.getPage(baseUrl + "news?p=" + pagination);
					System.out.println("TESSSTTTT ----- " + baseUrl + "news?p=" + pagination);
					items = (List<HtmlElement>) page.getByXPath("//tr[contains(@class, 'athing')]");
				}

				if (items.isEmpty()) {
					System.out.println("No posts found");
					break;
				}

				String postId = items.get(i).getId();

				Post post = new Post();
				DomElement firstRow = items.get(i).getLastElementChild().getFirstElementChild();
				post.setTitle(firstRow.asText());
				post.setUri(firstRow.getAttribute("href"));
				post.setRank(Integer.parseInt(items.get(i).getFirstElementChild().asText().split("[.]")[0]));

				try {
					String[] secondRow = page.getHtmlElementById("score_" + postId).getParentNode().asText()
							.split("[ ]");
					post.setAuthor(secondRow[3]);
					post.setPoints(Integer.parseInt(secondRow[0]));
					post.setComments(Integer.parseInt(secondRow[10]));

				} catch (ElementNotFoundException | NumberFormatException ex) {
				}

				UriChecker(post);
				StringHandler(post);
				IntHandler(post);
				printJSON(post);

			}

		} catch (IOException | FailingHttpStatusCodeException | ElementNotFoundException | DOMException e) {
		}
		client.close();
	}

	/**
	 * Check uri is a valid URI
	 */
	public void UriChecker(Post post) {
		try {
			URL url = new URL(post.getUri());
			url.toURI();
		} catch (MalformedURLException | URISyntaxException exception) {
			post.setUri("*invalid*");
		}
	}

	/**
	 * Check author and title are non empty strings not longer than 256 characters
	 */
	public void StringHandler(Post post) {
		if (post.getAuthor() == null || post.getAuthor().isEmpty()) {
			post.setAuthor("*undefined*");
		} else if ((post.getAuthor().length() > 256)) {
			post.setAuthor(post.getAuthor().substring(0, 256));
		}

		if (post.getTitle() == null || post.getTitle().isEmpty()) {
			post.setTitle("*undefined*");
		} else if ((post.getTitle().length() > 256)) {
			post.setTitle(post.getTitle().substring(0, 256));
		}
	}

	/**
	 * Check points, comments and rank are integers >= 0
	 */
	public void IntHandler(Post post) {
		if (post.getPoints() < 0) {
			post.setPoints(0);
		}
		if (post.getComments() < 0) {
			post.setComments(0);
		}
		if (post.getRank() < 0) {
			post.setRank(0);
		}
	}

	/**
	 * Print out posts output in JSON format
	 */
	public void printJSON(Post post) throws JsonProcessingException {

		ObjectMapper mapper = new ObjectMapper();
		String jsonString = mapper.writeValueAsString(post);
		System.out.println(jsonString);
	}
}
