{{/*
Expand the name of the chart.
*/}}
{{- define "spring-6-rest-mvc-mysql.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Create a default fully qualified app name.
We truncate at 63 chars because some Kubernetes name fields are limited to this (by the DNS naming spec).
If release name contains chart name it will be used as a full name.
*/}}
{{- define "spring-6-rest-mvc-mysql.fullname" -}}
{{- if .Values.fullnameOverride }}
{{- .Values.fullnameOverride | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- $name := default .Chart.Name .Values.nameOverride }}
{{- if contains $name .Release.Name }}
{{- .Release.Name | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- printf "%s-%s" .Release.Name $name | trunc 63 | trimSuffix "-" }}
{{- end }}
{{- end }}
{{- end }}

{{/*
Create chart name and version as used by the chart label.
*/}}
{{- define "spring-6-rest-mvc-mysql.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Common labels
*/}}
{{- define "spring-6-rest-mvc-mysql.labels" -}}
helm.sh/chart: {{ include "spring-6-rest-mvc-mysql.chart" . }}
{{ include "spring-6-rest-mvc-mysql.selectorLabels" . }}
{{- if .Chart.AppVersion }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
{{- end }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- end }}

{{/*
Selector labels
*/}}
{{- define "spring-6-rest-mvc-mysql.selectorLabels" -}}
app.kubernetes.io/name: {{ include "spring-6-rest-mvc-mysql.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end }}

{{/*
Create the FQDN for the service
*/}}
{{- define "spring-6-rest-mvc-mysql.serviceFQDN" -}}
{{- $fullname := include "spring-6-rest-mvc-mysql.fullname" . -}}
{{- printf "%s.%s.svc.cluster.local" $fullname .Release.Namespace }}
{{- end }}
