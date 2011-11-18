% This script demonstrates the use of the explicit simplex streams -
% Section 3.1

%% Circle Example

clc; clear; close all;

% get a new ExplicitSimplexStream
stream = api.Plex4.createExplicitSimplexStream();

% construct a triangle
stream.addVertex(0);
stream.addVertex(1);
stream.addVertex(2);
stream.addElement([0, 1]);
stream.addElement([0, 2]);
stream.addElement([1, 2]);
stream.finalizeStream();

% print out the total number of simplices in the complex
num_simplices = stream.getSize()

% get the default persistence computation
persistence = api.Plex4.getDefaultSimplicialAlgorithm(3);

% compute and print the intervals
circle_intervals = persistence.computeIntervals(stream)

%% 9-sphere Example

dimension = 9;

% get a new ExplicitSimplexStream
stream = api.Plex4.createExplicitSimplexStream();

% construct simplicial sphere
stream.addElement(0:(dimension + 1));
stream.ensureAllFaces();
stream.removeElementIfPresent(0:(dimension + 1));
stream.finalizeStream();

% print out the total number of simplices in the complex
num_simplices = stream.getSize()

% get the default persistence computation
persistence = api.Plex4.getDefaultSimplicialAlgorithm(dimension + 1);

% compute and print the intervals
n_sphere_intervals = persistence.computeIntervals(stream)
