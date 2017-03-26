/*
 * Copyright 2015 MongoDB, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mongodb.m101j.crud;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class FindTest {
    public static void main(String[] args) {
        MongoClient client = new MongoClient();
        MongoDatabase database = client.getDatabase("school");
        MongoCollection<Document> collection = database.getCollection("students");

        List<Document> all = collection.find().into(new ArrayList<Document>());
        for (Document cur : all) {
            int docId = cur.getInteger("_id");
            List<Document> scores = cur.get("scores", List.class);
            double lowest = 100;
            int lowIdx = -1;
            for (Document score : scores) {
                if (score.getString("type").equals("homework")) {
                    double scorePoint = score.getDouble("score");
                    if (lowest > scorePoint) {
                        lowest = scorePoint;
                        lowIdx = scores.indexOf(score);
                    }
                }                
            }
            scores.remove(lowIdx);
            
            collection.updateOne(eq("_id", docId), new Document("$set", new Document("scores", scores)));
        }
    }
}
