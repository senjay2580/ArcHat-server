package com.senjay.archat;

import java.text.Normalizer;
import java.util.*;

public class SensitiveWordDetector {

    private final TrieNode root = new TrieNode();

    public SensitiveWordDetector(List<String> keywords) {
        for (String word : keywords) {
            insert(preprocessText(word));
        }
        buildFailPointers();
    }

    private void insert(String word) {
        TrieNode node = root;
        for (char c : word.toCharArray()) {
            node = node.children.computeIfAbsent(c, k -> new TrieNode());
        }
        node.isEnd = true;
    }

    private void buildFailPointers() {
        Queue<TrieNode> queue = new LinkedList<>();
        root.fail = root;
        queue.add(root);

        while (!queue.isEmpty()) {
            TrieNode cur = queue.poll();
            for (Map.Entry<Character, TrieNode> entry : cur.children.entrySet()) {
                char c = entry.getKey();
                TrieNode child = entry.getValue();

                TrieNode failNode = cur.fail;
                while (failNode != root && !failNode.children.containsKey(c)) {
                    failNode = failNode.fail;
                }
                if (failNode.children.containsKey(c) && failNode.children.get(c) != child) {
                    child.fail = failNode.children.get(c);
                } else {
                    child.fail = root;
                }
                queue.add(child);
            }
        }
    }

    public boolean isNegativeComment(String comment) {
        if (comment == null || comment.isEmpty()) return false;
        String normalized = preprocessText(comment);

        TrieNode node = root;
        for (char c : normalized.toCharArray()) {
            while (node != root && !node.children.containsKey(c)) {
                node = node.fail;
            }
            node = node.children.getOrDefault(c, root);

            TrieNode tmp = node;
            while (tmp != root) {
                if (tmp.isEnd) return true;
                tmp = tmp.fail;
            }
        }
        return false;
    }

    // 预处理：去标点、空格、符号，统一全角半角
    private static String preprocessText(String text) {
        if (text == null) return "";
        // NFKC 规范化全角半角
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFKC);
        // 去掉所有非文字字符，只保留中英文汉字数字
        normalized = normalized.replaceAll("[^\\p{L}\\p{N}]", "");
        return normalized;
    }

    static class TrieNode {
        Map<Character, TrieNode> children = new HashMap<>();
        TrieNode fail;
        boolean isEnd;
    }

    public static void main(String[] args) {
        List<String> sensitiveWords = List.of(
                "垃圾", "破烂", "差评", "不行", "没用"
        );

        SensitiveWordDetector detector = new SensitiveWordDetector(sensitiveWords);

        String[] comments = {
                "这商品真垃圾！！！",
                "这真是个好产品！",
                "垃。。圾，没法用"
        };

        for (String c : comments) {
            System.out.printf("【%s】 => %s%n", c, detector.isNegativeComment(c));
        }
    }
}
