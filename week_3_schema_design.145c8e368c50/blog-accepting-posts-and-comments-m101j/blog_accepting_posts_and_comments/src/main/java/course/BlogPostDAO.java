package course;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.Updates;
import java.util.ArrayList;
import java.util.Date;
import org.bson.Document;

import java.util.List;

public class BlogPostDAO {
    MongoCollection<Document> postsCollection;

    public BlogPostDAO(final MongoDatabase blogDatabase) {
        postsCollection = blogDatabase.getCollection("posts");
    }

    // Return a single post corresponding to a permalink
    public Document findByPermalink(String permalink) {
        Document post = postsCollection
                .find(Filters.eq("permalink", permalink))
                .first();
        return post;
    }

    // Return a list of posts in descending order. Limit determines
    // how many posts are returned.
    public List<Document> findByDateDescending(int limit) {

        List<Document> posts = postsCollection
                .find()
                .sort(Sorts.descending("date"))
                .limit(limit)
                .into(new ArrayList<Document>());

        return posts;
    }


    public String addPost(String title, String body, List tags, String username) {

        System.out.println("inserting blog entry " + title + " " + body);

        String permalink = title.replaceAll("\\s", "_"); // whitespace becomes _
        permalink = permalink.replaceAll("\\W", ""); // get rid of non alphanumeric
        permalink = permalink.toLowerCase();

        Document post = new Document()
                .append("title", title)
                .append("body", body)
                .append("author", username)
                .append("permalink", permalink)
                .append("tags", tags)
                .append("comments", new ArrayList<String>())
                .append("date", new Date());

        postsCollection.insertOne(post);
        return permalink;
    }
    
    // Append a comment to a blog post
    public void addPostComment(final String name, final String email, final String body,
                               final String permalink) {

        // XXX HW 3.3, Work Here
        // Hints:
        // - email is optional and may come in NULL. Check for that.
        // - best solution uses an update command to the database and a suitable
        //   operator to append the comment on to any existing list of comments
        System.out.println("inserting blog comment " + name + " " + body);

        Document comment = new Document()
                .append("author", name)
                .append("body", body);
        
        if (email != null) {
            comment.append("email", email);
        }

        postsCollection.updateOne(
                Filters.eq("permalink", permalink), 
                Updates.push("comments", comment));        
    }
}
