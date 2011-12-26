public interface ProjectVisitor{
    public void visitDependentTask(DependentTask p);
    public void visitDeliverable(Deliverable p);
    public void visitTask(Task p);
    public void visitProject(Project p);
}