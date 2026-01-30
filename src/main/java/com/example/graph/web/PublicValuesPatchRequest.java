package com.example.graph.web;

import com.example.graph.web.form.EdgeValueForm;
import com.example.graph.web.form.NodeValueForm;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PublicValuesPatchRequest {
    private NodeValueForm nodeValue;
    private EdgeValueForm edgeValue;
}
