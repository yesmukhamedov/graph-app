package com.example.graph.converter;

import com.example.graph.model.EdgeEntity;
import com.example.graph.model.NodeEntity;
import com.example.graph.model.phone.PhoneEntity;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class JsonLdConverter {
    public Map<String, Object> toJsonLd(GraphSnapshot snapshot) {
        List<Map<String, Object>> graph = new ArrayList<>();
        for (NodeEntity node : snapshot.getNodes()) {
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("@id", "node:" + node.getId());
            entry.put("@type", "Node");
            String value = snapshot.getNodeValues().get(node.getId());
            if (value != null) {
                entry.put("value", value);
            }
            graph.add(entry);
        }

        for (EdgeEntity edge : snapshot.getEdges()) {
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("@id", "edge:" + edge.getId());
            entry.put("@type", "Edge");
            entry.put("kind", resolveKind(edge));
            if (edge.getFromNode() != null) {
                entry.put("from", "node:" + edge.getFromNode().getId());
            }
            if (edge.getToNode() != null) {
                entry.put("to", "node:" + edge.getToNode().getId());
            }
            String value = snapshot.getEdgeValues().get(edge.getId());
            if (value != null) {
                entry.put("value", value);
            }
            if (edge.getCreatedAt() != null) {
                entry.put("createdAt", edge.getCreatedAt());
            }
            if (edge.getExpiredAt() != null) {
                entry.put("expiredAt", edge.getExpiredAt());
            }
            graph.add(entry);
        }

        for (PhoneEntity phone : snapshot.getPhones()) {
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("@id", "phone:" + phone.getId());
            entry.put("@type", "Phone");
            entry.put("node", "node:" + phone.getNode().getId());
            entry.put("pattern", phone.getPattern().getCode());
            String value = snapshot.getPhoneValues().get(phone.getId());
            if (value != null) {
                entry.put("value", value);
            }
            graph.add(entry);
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("@context", buildContext());
        response.put("@graph", graph);
        return response;
    }

    private Map<String, Object> buildContext() {
        Map<String, Object> context = new LinkedHashMap<>();
        context.put("@vocab", "https://example.org/shezhire#");
        context.put("Node", "Node");
        context.put("Edge", "Edge");
        context.put("Phone", "Phone");
        context.put("id", "@id");
        context.put("type", "@type");
        Map<String, Object> from = new LinkedHashMap<>();
        from.put("@id", "from");
        from.put("@type", "@id");
        context.put("from", from);
        Map<String, Object> to = new LinkedHashMap<>();
        to.put("@id", "to");
        to.put("@type", "@id");
        context.put("to", to);
        Map<String, Object> hasPhone = new LinkedHashMap<>();
        hasPhone.put("@id", "hasPhone");
        hasPhone.put("@type", "@id");
        context.put("hasPhone", hasPhone);
        return context;
    }

    private String resolveKind(EdgeEntity edge) {
        if (edge.isCategory()) {
            return "Category";
        }
        if (edge.isNote()) {
            return "Note";
        }
        return "Relation";
    }
}
